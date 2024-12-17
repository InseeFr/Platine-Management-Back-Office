package fr.insee.survey.datacollectionmanagement.query.repository;

import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MoogRepository {

    private final JdbcTemplate jdbcTemplate;

    private final AddressRepository addressRepository;

    final String getEventsQuery = "SELECT qe.id, date, type, survey_unit_id_su, campaign_id "
            + " FROM questioning_event qe join questioning q on qe.questioning_id=q.id join partitioning p on q.id_partitioning=p.id "
            + " WHERE survey_unit_id_su=? AND campaign_id=? ";


    public List<MoogQuestioningEventDto> getEventsByIdSuByCampaign(String idCampaign, String idSu) {
        return jdbcTemplate.query(getEventsQuery, new RowMapper<MoogQuestioningEventDto>() {
            public MoogQuestioningEventDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                MoogQuestioningEventDto moogEvent = new MoogQuestioningEventDto();
                moogEvent.setIdManagementMonitoringInfo(rs.getString("id"));
                moogEvent.setStatus(rs.getString("type"));
                moogEvent.setDateInfo(rs.getTimestamp("date").getTime());
                return moogEvent;
            }
        }, new Object[]{idSu, idCampaign});
    }

    final String extractionQuery = """
            SELECT
                                   B.id_su,
                                   B.identifier AS id_contact,
                                   B.first_name AS firstname,
                                   B.last_name AS lastname,
                                   addr.id as address_id,
                                   addr.street_number as street_number,
                                   addr.repetition_index as repetition_index,
                                   addr.street_type as street_type,
                                   addr.street_name as street_name,
                                   addr.address_supplement as address_supplement ,
                           		   addr.zip_code as zip_code ,
                                   addr.city_name as city_name,
                                   addr.cedex_code as cedex_code,
                                   addr.cedex_name as cedex_name,
                                   addr.special_distribution as special_distribution,
                                   addr.country_code as country_code,
                                   addr.country_name as country_name,
                                   B.date AS dateinfo,
                                   B.type AS status,
                                   B.batch_num
                               FROM (
                                   SELECT
                                       q.id,
                                       A.campaign_id,
                                       A.id_su,
                                       A.identifier,
                                       A.first_name,
                                       A.last_name,
                                       A.address_id,
                                       q.id_partitioning AS batch_num,
                                       qe.date,
                                       qe.type
                                   FROM (
                                       SELECT
                                           v.campaign_id,
                                           v.id_su,
                                           contact.identifier,
                                           contact.first_name,
                                           contact.last_name,
                                           contact.address_id
                                       FROM view v
                                       LEFT JOIN contact ON contact.identifier = v.identifier
                                       WHERE v.campaign_id = ?
                                   ) AS A
                                   LEFT JOIN questioning q
                                       ON A.id_su = q.survey_unit_id_su
                                       AND q.id_partitioning IN (
                                           SELECT id
                                           FROM partitioning p
                                           WHERE p.campaign_id = ?
                                       )
                                   LEFT JOIN questioning_event qe
                                       ON q.id = qe.questioning_id
                               ) AS B
                               LEFT JOIN address addr
                                   ON B.address_id = addr.id
            """;


    public List<MoogExtractionRowDto> getExtraction(String idCampaign) {
        return jdbcTemplate.query(extractionQuery, (rs, rowNum) -> {
            MoogExtractionRowDto ev = new MoogExtractionRowDto();

            ev.setAddress("addresse non connue");

            ev.setStatus(rs.getString("status"));
            ev.setDateInfo(rs.getString("dateinfo"));
            ev.setIdSu(rs.getString("id_su"));
            ev.setIdContact(rs.getString("id_contact"));
            ev.setLastname(rs.getString("lastname"));
            ev.setFirstname(rs.getString("firstname"));
            ev.setAddress(createAddress(rs));
            /*addressRepository
                    .findById(rs.getString("address"))
                    .ifPresentOrElse(
                            address -> ev.setAddress(address.toStringMoog()),
                            () -> log.info("Address not found")
                    );*/

            ev.setBatchNumber(rs.getString("batch_num"));

            return ev;
        }, new Object[]{idCampaign, idCampaign});
    }

    private static String createAddress(ResultSet rs) throws SQLException {
        StringBuilder addressBuilder = new StringBuilder();

        appendIfNotEmpty(addressBuilder, rs.getString("street_number"));
        appendIfNotEmpty(addressBuilder, rs.getString("repetition_index"));
        appendIfNotEmpty(addressBuilder, rs.getString("street_type"));
        appendIfNotEmpty(addressBuilder, rs.getString("street_name"));
        appendIfNotEmpty(addressBuilder, rs.getString("address_supplement"));
        appendIfNotEmpty(addressBuilder, rs.getString("zip_code"));
        appendIfNotEmpty(addressBuilder, rs.getString("city_name"));
        appendIfNotEmpty(addressBuilder, rs.getString("cedex_code"));
        appendIfNotEmpty(addressBuilder, rs.getString("cedex_name"));
        appendIfNotEmpty(addressBuilder, rs.getString("special_distribution"));
        appendIfNotEmpty(addressBuilder, rs.getString("country_code"));
        appendIfNotEmpty(addressBuilder, rs.getString("country_name"));

        return addressBuilder.toString().trim(); // Remove trailing spaces
    }

    private static void appendIfNotEmpty(StringBuilder builder, String value) {
        if (value != null && !value.isEmpty()) {
            builder.append(value.trim()).append(" ");
        }
    }

    final String surveyUnitFollowUpQuery = """
                select
                    distinct on
                    (id_su) id_su,
                    batch_num,
                    case
                        when type in ('PND') then 1
                        else 0
                    end as PND
                from
                    (
                    select
                        A.id_su,
                        A.identifier,
                        q.id,
                        q.id_partitioning as batch_num
                    from
                        (
                        select
                            id_su,
                            identifier
                        from
                            public.view v
                        where
                            campaign_id = ?)as A
                    left join questioning q on
                        q.survey_unit_id_su = A.id_su
                        and q.id_partitioning in (
                        select
                            id
                        from
                            partitioning p
                        where
                            p.campaign_id = ?)) as B
                left join questioning_event qe on
                    B.id = qe.questioning_id
                where
                    B.id_su not in (
                    select
                        distinct on
                        (id_su) id_su
                    from
                        (
                        select
                            id_su,
                            identifier,
                            id,
                            id_partitioning as batch_num
                        from
                            (
                            select
                                id_su,
                                identifier
                            from
                                public.view
                            where
                                campaign_id = ?)as A
                        left join questioning q on
                            q.survey_unit_id_su = A.id_su
                            and q.id_partitioning in (
                            select
                                id
                            from
                                partitioning p
                            where
                                p.campaign_id = ?)) as B
                    left join questioning_event on
                        B.id = questioning_event.questioning_id
                    where
                        type in ('VALINT', 'VALPAP', 'HC', 'REFUSAL', 'WASTE'))
                order by
                    id_su,
                    pnd desc;
            """;

    public List<MoogExtractionRowDto> getSurveyUnitToFollowUp(String idCampaign) {
        return jdbcTemplate.query(surveyUnitFollowUpQuery,
                new RowMapper<>() {
                    public MoogExtractionRowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        MoogExtractionRowDto er = new MoogExtractionRowDto();
                        er.setIdSu(rs.getString("id_su"));
                        er.setPnd(rs.getInt("PND"));
                        er.setBatchNumber(rs.getString("batch_num"));

                        return er;
                    }
                }, new Object[]{idCampaign, idCampaign, idCampaign, idCampaign});
    }
}
