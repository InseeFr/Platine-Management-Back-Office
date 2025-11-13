package fr.insee.survey.datacollectionmanagement.questioning.dao.search;

import fr.insee.survey.datacollectionmanagement.query.dto.LastCommunicationDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.WalletFilterEnum;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static fr.insee.survey.datacollectionmanagement.questioning.enums.WalletFilterEnum.GROUPS;
import static fr.insee.survey.datacollectionmanagement.questioning.enums.WalletFilterEnum.MY_WALLET;

@Repository
@RequiredArgsConstructor
public class SearchQuestioningDao {
    private final EntityManager entityManager;

    public Slice<SearchQuestioningDto> search(SearchQuestioningParams searchQuestioningParams, Pageable pageable, String userId) {
        SearchFilter filterQuestionings = buildQuestioningsFilter(searchQuestioningParams.userOrSurveyUnitId());
        String joinQuestionings = buildQuestioningsJoin(searchQuestioningParams.userOrSurveyUnitId());
        SearchFilter filterCampaigns = buildCampaignFilter(searchQuestioningParams.campaignIds());
        SearchFilter filterTypes = buildTypesFilter(searchQuestioningParams.typeQuestioningEvents(),
                searchQuestioningParams.typeCommunicationEvents());
        SearchFilter filterWallet = buildWalletFilter(searchQuestioningParams.walletFilter(), userId);
        StringBuilder sql = new StringBuilder("WITH ");
        Map<String, Object> parameters = buildParameters(List.of(filterQuestionings, filterCampaigns, filterTypes, filterWallet), pageable);

        // filter questioning ids by searching on identification, id_su or id_contact
        sql.append(filterQuestionings.sqlFilter());
        sql.append(" ");
        sql.append("""
            qlimited AS (
                SELECT
                    q.id AS questioning_id,
                    p.campaign_id,
                 (
                     SELECT qc.type
                     FROM questioning_communication qc
                     WHERE qc.questioning_id = q.id
                     ORDER BY qc.date DESC
                     LIMIT 1
                 ) AS last_communication_type,
                (
                     SELECT qc.with_receipt
                     FROM questioning_communication qc
                     WHERE qc.questioning_id = q.id
                     ORDER BY qc.date DESC
                     LIMIT 1
                 ) AS last_communication_receipt,
                 (
                     SELECT qc.with_questionnaire
                     FROM questioning_communication qc
                     WHERE qc.questioning_id = q.id
                     ORDER BY qc.date DESC
                     LIMIT 1
                 ) AS last_communication_questionnaire ,
                    q.highest_event_type AS highest_event_type,
                    CASE
                       WHEN q.highest_event_type IN ('VALINT', 'VALPAP') THEN q.highest_event_date
                       ELSE NULL
                    END AS validation_date,
                    su.id_su AS survey_unit_id,
                    su.identification_code AS identification_code,
                    q.score AS score
            """);

        sql.append(" ");
        sql.append(joinQuestionings);
        sql.append(" ");
        sql.append("""
            JOIN survey_unit su
                 ON q.survey_unit_id_su = su.id_su
            JOIN partitioning p
                ON q.id_partitioning = p.id
            JOIN campaign c
                ON c.id=p.campaign_id
            JOIN survey sv
                ON sv.id =c.survey_id
            JOIN source sc
                ON sc.id=sv.source_id
        """);
        sql.append(" ");
        sql.append(filterWallet.sqlFilter());
        sql.append(" ");
        sql.append(filterCampaigns.sqlFilter());
        sql.append(" ");
        sql.append(filterTypes.sqlFilter());
        sql.append(" ORDER BY ");
        if (pageable.getSort().isSorted()) {
            List<String> orderClauses = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                String direction = order.isAscending() ? "ASC" : "DESC";
                orderClauses.add(order.getProperty() + " " + direction + " NULLS LAST");
            }
            sql.append(String.join(", ", orderClauses));
            sql.append(", ");
        }
        sql.append("q.id ASC");
        sql.append("""
                LIMIT :size OFFSET :offset
            )
            SELECT
                qlimited.questioning_id,
                qlimited.campaign_id,
                qlimited.last_communication_type,
                qlimited.last_communication_receipt,
                qlimited.last_communication_questionnaire,
                qlimited.validation_date,
                qlimited.highest_event_type,
                qlimited.survey_unit_id,
                qlimited.identification_code,
                qa_all.id_contact AS contact_id,
                qlimited.score
            FROM qlimited
            LEFT JOIN questioning_accreditation qa_all
                ON qa_all.questioning_id = qlimited.questioning_id;""");
        var nativeQuery = entityManager.createNativeQuery(sql.toString());
        parameters.forEach(nativeQuery::setParameter);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = nativeQuery.getResultList();

        return buildResult(rows, pageable);
    }


    private Slice<SearchQuestioningDto> buildResult(List<Object[]> rows, Pageable pageable) {

        if(rows.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        List<SearchQuestioningDto> results = new ArrayList<>();
        for(Object[] row : rows) {
            UUID questioningId = buildQuestioningId(row[0]);
            Optional<SearchQuestioningDto> optSearchQuestioningResult = results.stream()
                    // filter on questioning id
                    .filter(result -> questioningId.equals(result.getQuestioningId()))
                    .findFirst();

            if(optSearchQuestioningResult.isEmpty()) {
                SearchQuestioningDto result = mapRowToDto(row);
                results.add(result);
                continue;
            }

            SearchQuestioningDto result = optSearchQuestioningResult.get();
            // add contact id
            result.addContactId((String)row[9]);
        }

        // check if there is a next page
        boolean hasNextPage = false;
        if(results.size() > pageable.getPageSize()) {
            hasNextPage = true;
            results.removeLast();
        }

        return new SliceImpl<>(results, pageable, hasNextPage);
    }

    private Map<String, Object> buildParameters(List<SearchFilter> filterResults, Pageable pageable) {
        if (filterResults == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> parameters = filterResults.stream()
                .flatMap(result -> result.parameters().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap::new
                ));

        // need the +1 to see if there is a next page
        parameters.put("size", pageable.getPageSize() + 1);
        parameters.put("offset", pageable.getOffset());
        return parameters;
    }

    private String buildQuestioningsJoin(String id) {
        if(id == null || id.isBlank()) {
            return "FROM questioning q";
        }

        return """
                FROM filtered_questionings JOIN questioning q
                    ON q.id = filtered_questionings.questioning_id""";
    }

    private SearchFilter buildQuestioningsFilter(String id) {
        if(id == null || id.isBlank()) {
            return new SearchFilter("", Map.of());
        }

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("id", id);

        String sqlFilter = """
                filtered_questionings AS (
                  (
                    SELECT q.id AS questioning_id
                    FROM survey_unit su
                    JOIN questioning q
                     ON q.survey_unit_id_su = su.id_su
                     AND (
                        su.identification_code = :id
                        OR su.identification_name = :id
                        OR su.id_su             = :id
                     )
                  )
                  UNION ALL
                  (
                    SELECT qa.questioning_id
                    FROM questioning_accreditation qa
                    WHERE qa.id_contact = :id
                  )
                ),""";
        return new SearchFilter(sqlFilter, parameters);
    }

    private UUID buildQuestioningId(Object rawId) {
        return switch (rawId) {
            case UUID uuid -> uuid;
            case byte[] bytes -> {
                // if DB returns UUID in binary format
                ByteBuffer bb = ByteBuffer.wrap(bytes);
                yield new UUID(bb.getLong(), bb.getLong());
            }
            case String str -> UUID.fromString(str);
            default -> throw new IllegalArgumentException("Unexpected type for id : " + rawId.getClass());
        };
    }

    private SearchFilter buildCampaignFilter(List<String> campaignIds) {
        if (campaignIds == null || campaignIds.isEmpty()) {
            return new SearchFilter("", Map.of());
        }

        List<String> paramNames = new ArrayList<>();
        Map<String, Object> parameters = new LinkedHashMap<>();
        for (int i = 0; i < campaignIds.size(); i++) {
            String paramName = "campaign" + i;
            paramNames.add(paramName);
            parameters.put(paramName, campaignIds.get(i).toUpperCase());
        }

        String placeholders = paramNames.stream()
                .map(name -> ":" + name)
                .collect(Collectors.joining(", "));

        String filter = "AND p.campaign_id IN (" + placeholders + ")";

        return new SearchFilter(filter, parameters);
    }

    private SearchFilter buildTypesFilter(List<TypeQuestioningEvent> typeQuestioningEvents,
                                                    List<TypeCommunicationEvent> typeCommunicationEvents) {
        Optional<SearchFilter> optionalEventsFilter = buildEventFilter(typeQuestioningEvents);
        Optional<SearchFilter> optionalCommFilter = buildCommunicationFilter(typeCommunicationEvents);
        String whereClause = "AND ";

        if(optionalEventsFilter.isEmpty() && optionalCommFilter.isEmpty()) {
            return new SearchFilter("", Map.of());
        }

        if(optionalEventsFilter.isEmpty()) {
            SearchFilter commResult = optionalCommFilter.get();
            return new SearchFilter(whereClause + commResult.sqlFilter(),
                    commResult.parameters());
        }

        if(optionalCommFilter.isEmpty()) {
            SearchFilter eventResult = optionalEventsFilter.get();
            return new SearchFilter(whereClause + eventResult.sqlFilter(),
                    eventResult.parameters());
        }

        SearchFilter commResult = optionalCommFilter.get();
        SearchFilter eventResult = optionalEventsFilter.get();

        String mergedSql = whereClause + eventResult.sqlFilter() + " AND " + commResult.sqlFilter();
        Map<String, Object> mergedProperties = new HashMap<>();
        mergedProperties.putAll(eventResult.parameters());
        mergedProperties.putAll(commResult.parameters());
        return new SearchFilter(mergedSql, mergedProperties);
    }

    private SearchFilter buildWalletFilter(WalletFilterEnum walletType, String userId) {

        if (walletType.equals(MY_WALLET)){
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("user_id", userId);

            String sqlFilter = """
            JOIN user_wallet uw
                ON uw.survey_unit_id = su.id_su
                AND uw.source_id = sc.id
                AND UPPER(uw.user_id) = :user_id
            """;
            return new SearchFilter(sqlFilter, parameters);
        }
        if (walletType.equals(GROUPS)) {
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("user_id", userId);

            String sqlFilter = """
            JOIN group_wallet gw
                ON gw.survey_unit_id = su.id_su
            JOIN groups g
                ON g.group_id = gw.group_id
                AND g.source_id = sc.id
            JOIN user_group ug
                ON ug.group_id = g.group_id
                AND UPPER(ug.user_id) = :user_id
            """;
            return new SearchFilter(sqlFilter, parameters);

        }
        return new SearchFilter("", Map.of());

    }


    private Optional<SearchFilter> buildCommunicationFilter(List<TypeCommunicationEvent> typeCommunicationEvents) {
        if (typeCommunicationEvents == null || typeCommunicationEvents.isEmpty()) {
            return Optional.empty();
        }

        List<String> paramNames = new ArrayList<>();
        Map<String, Object> parameters = new LinkedHashMap<>();
        for (int i = 0; i < typeCommunicationEvents.size(); i++) {
            String paramName = "comm" + i;
            paramNames.add(paramName);
            parameters.put(paramName, typeCommunicationEvents.get(i).name());
        }

        String placeholders = paramNames.stream()
                .map(name -> ":" + name)
                .collect(Collectors.joining(", "));

        String filter = """
        (
          SELECT qc.type
          FROM questioning_communication qc
          WHERE qc.questioning_id = q.id
          ORDER BY qc.date DESC
          LIMIT 1
        ) IN (""" + placeholders + ")";

        return Optional.of(new SearchFilter(filter, parameters));
    }

    private Optional<SearchFilter> buildEventFilter(List<TypeQuestioningEvent> typeQuestioningEvents) {
        if (typeQuestioningEvents == null || typeQuestioningEvents.isEmpty()) {
            return Optional.empty();
        }

        List<String> paramNames = new ArrayList<>();
        Map<String, Object> parameters = new LinkedHashMap<>();
        for (int i = 0; i < typeQuestioningEvents.size(); i++) {
            String paramName = "event" + i;
            paramNames.add(paramName);
            parameters.put(paramName, typeQuestioningEvents.get(i).name());
        }

        String placeholders = paramNames.stream()
                .map(name -> ":" + name)
                .collect(Collectors.joining(", "));

        String filter = " q.highest_event_type IN (" + placeholders + ")";

        return Optional.of(new SearchFilter(filter, parameters));
    }

    private SearchQuestioningDto mapRowToDto(Object[] row) {
        UUID questioningId = buildQuestioningId(row[0]);
        String campaignId = (String) row[1];
        String lastCommunicationType = (String) row[2];
        boolean lastCommunicationReceipt = row[3] != null && (boolean) row[3];
        boolean lastCommunicationQuestionnaire = row[4] != null && (boolean) row[4];
        Date validationDate = (Date) row[5];
        String highestEventType = (String) row[6];
        String surveyUnitId = (String) row[7];
        String identificationCode = (String) row[8];
        String contactId = (String) row[9];
        Integer score = (Integer) row[10];

        TypeCommunicationEvent typeCommunicationEvent = lastCommunicationType != null ? TypeCommunicationEvent.valueOf(lastCommunicationType) : null;
        TypeQuestioningEvent typeQuestioningEvent = highestEventType != null ? TypeQuestioningEvent.valueOf(highestEventType) : null;

        LastCommunicationDto lastCommunicationDto = new LastCommunicationDto(typeCommunicationEvent, lastCommunicationReceipt, lastCommunicationQuestionnaire);
        return new SearchQuestioningDto(
                questioningId,
                campaignId,
                lastCommunicationDto,
                validationDate,
                typeQuestioningEvent,
                surveyUnitId,
                identificationCode,
                contactId,
                score
        );
    }
}
