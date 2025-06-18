package fr.insee.survey.datacollectionmanagement.questioning.dao.search;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchQuestioningDao {
    private final EntityManager entityManager;

    public Slice<SearchQuestioningDto> search(SearchQuestioningParams searchQuestioningParams, Pageable pageable) {
        SearchFilter filterQuestionings = buildQuestioningsFilter(searchQuestioningParams.userOrSurveyUnitId());
        String joinQuestionings = buildQuestioningsJoin(searchQuestioningParams.userOrSurveyUnitId());
        SearchFilter filterCampaigns = buildCampaignFilter(searchQuestioningParams.campaignIds());
        SearchFilter filterTypes = buildTypesFilter(searchQuestioningParams.typeQuestioningEvents(),
                searchQuestioningParams.typeCommunicationEvents());
        StringBuilder sql = new StringBuilder("WITH ");
        Map<String, Object> parameters = buildParameters(List.of(filterQuestionings, filterCampaigns, filterTypes), pageable);

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
                        SELECT qe.type
                        FROM questioning_event qe
                        JOIN interrogation_event_order ie
                        ON ie.status = qe.type
                        WHERE qe.questioning_id = q.id
                        ORDER BY ie.event_order DESC, qe.date DESC
                        LIMIT 1
                    ) AS highest_event_type,
                    (
                        SELECT qe2.date
                        FROM questioning_event qe2
                        WHERE qe2.questioning_id = q.id
                        AND qe2.type IN ('VALINT','VALPAP')
                        ORDER BY qe2.date DESC
                        LIMIT 1
                    ) AS validation_date,
                    su.id_su AS survey_unit_id,
                    su.identification_code AS identification_code""");
        sql.append(" ");
        sql.append(joinQuestionings);
        sql.append(" ");
        sql.append("""
                JOIN survey_unit su
                    ON q.survey_unit_id_su = su.id_su
                JOIN partitioning p
                    ON q.id_partitioning = p.id""");
        sql.append(" ");
        sql.append(filterCampaigns.sqlFilter());
        sql.append(" ");
        sql.append(filterTypes.sqlFilter());
        sql.append(" ");
        sql.append("""
                LIMIT :size OFFSET :offset
            )
            SELECT
                qlimited.questioning_id,
                qlimited.campaign_id,
                qlimited.last_communication_type,
                qlimited.validation_date,
                qlimited.highest_event_type,
                qlimited.survey_unit_id,
                qlimited.identification_code,
                qa_all.id_contact AS contact_id
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
            Optional<SearchQuestioningDto> optSearchQuestioningResult = results.stream()
                    // filter on questioning id
                    .filter(result -> row[0].equals(result.getQuestioningId()))
                    .findFirst();

            if(optSearchQuestioningResult.isEmpty()) {
                SearchQuestioningDto result = mapRowToDto(row);
                results.add(result);
                continue;
            }

            SearchQuestioningDto result = optSearchQuestioningResult.get();
            // add contact id
            result.addContactId((String)row[7]);
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
        String whereClause = "WHERE ";

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

        String filter = """
                (
                    SELECT qe.type
                    FROM questioning_event qe
                    JOIN interrogation_event_order ie
                      ON ie.status = qe.type
                    WHERE qe.questioning_id = q.id
                    ORDER BY ie.event_order DESC, qe.date DESC
                    LIMIT 1
                ) IN (""" + placeholders + ")";

        return Optional.of(new SearchFilter(filter, parameters));
    }

    private static SearchQuestioningDto mapRowToDto(Object[] row) {
        Long questioningId = (Long) row[0];
        String campaignId = (String) row[1];
        String lastCommunicationType = (String) row[2];
        Date validationDate = (Date) row[3];
        String highestEventType = (String) row[4];
        String surveyUnitId = (String) row[5];
        String identificationCode = (String) row[6];
        String contactId = (String) row[7];

        TypeCommunicationEvent typeCommunicationEvent = lastCommunicationType != null ? TypeCommunicationEvent.valueOf(lastCommunicationType) : null;
        TypeQuestioningEvent typeQuestioningEvent = highestEventType != null ? TypeQuestioningEvent.valueOf(highestEventType) : null;

        return new SearchQuestioningDto(
                questioningId,
                campaignId,
                typeCommunicationEvent,
                validationDate,
                typeQuestioningEvent,
                surveyUnitId,
                identificationCode,
                contactId
        );
    }
}
