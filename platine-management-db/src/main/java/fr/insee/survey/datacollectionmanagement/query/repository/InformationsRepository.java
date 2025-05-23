package fr.insee.survey.datacollectionmanagement.query.repository;

import fr.insee.survey.datacollectionmanagement.query.domain.QuestioningInformations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InformationsRepository {

    private final JdbcTemplate jdbcTemplate;
    static final String GET_QUESTIONING_INFORMATIONS_INTERVIEWER = """
                SELECT
                    c.identifier,
                    c.gender,
                    c.first_name,
                    c.last_name,
                    c.email AS email,
                    c.phone,
                    c.phone2,
                    c.usual_company_name,
                    a.*,
                    su.id_su,
                    su.label,
                    su.identification_code,
                    su.identification_name,
                    p.return_date,
                    s2.logo,
                    q.id AS questioning_id,
                    s2.id AS source_id
                FROM
                    questioning q
                JOIN questioning_accreditation qa ON q.id = qa.questioning_id
                JOIN contact c ON c.identifier = qa.id_contact
                JOIN address a ON c.address_id = a.id
                JOIN survey_unit su ON su.id_su = q.survey_unit_id_su
                JOIN partitioning p ON p.id = q.id_partitioning
                JOIN campaign c2 ON c2.id = p.campaign_id
                JOIN survey s ON s.id = c2.survey_id
                JOIN source s2 ON s2.id = s.source_id
                WHERE
                    q.id_partitioning = ?
                    AND q.survey_unit_id_su = ?
                    AND qa.id_contact = ?
            """;

    static final String GET_QUESTIONING_INFORMATIONS_REVIEWER = """
            SELECT
                c.identifier,
                c.gender,
                c.first_name,
                c.last_name,
                c.email AS email,
                c.phone,
                c.phone2,
                c.usual_company_name,
                a.*,
                su.id_su,
                su.label,
                su.identification_code,
                su.identification_name,
                p.return_date,
                s2.logo,
                q.id AS questioning_id,
                s2.id AS source_id
            FROM
                questioning q
            JOIN questioning_accreditation qa ON q.id = qa.questioning_id
            JOIN contact c ON c.identifier = qa.id_contact
            JOIN address a ON c.address_id = a.id
            JOIN survey_unit su ON su.id_su = q.survey_unit_id_su
            JOIN partitioning p ON p.id = q.id_partitioning
            JOIN campaign c2 ON c2.id = p.campaign_id
            JOIN survey s ON s.id = c2.survey_id
            JOIN source s2 ON s2.id = s.source_id
            WHERE
                q.id_partitioning = ?
                AND q.survey_unit_id_su = ?
                AND qa.is_main = TRUE
            """;

    public QuestioningInformations findQuestioningInformationsInterviewer(String idPartitioning, String idSu, String contactId) {

        return jdbcTemplate.queryForObject(GET_QUESTIONING_INFORMATIONS_INTERVIEWER, new BeanPropertyRowMapper<>(QuestioningInformations.class), idPartitioning, idSu, contactId);

    }

    public QuestioningInformations findQuestioningInformationsReviewer(String idPartitioning, String idSu) {

        return jdbcTemplate.queryForObject(GET_QUESTIONING_INFORMATIONS_REVIEWER, new BeanPropertyRowMapper<>(QuestioningInformations.class), idPartitioning, idSu);

    }


}
