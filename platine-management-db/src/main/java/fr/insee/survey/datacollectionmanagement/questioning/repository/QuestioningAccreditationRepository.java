package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestioningAccreditationRepository extends JpaRepository<QuestioningAccreditation, Long> {

    List<QuestioningAccreditation> findByIdContact(String idContact);

    @Query(value = "SELECT " +
            "so.id AS sourceId, " +
            "s.year_value AS surveyYear, " +
            "c.period_collect_value AS periodCollect, " +
            "q.id AS questioningId, " +
            "p.label AS partitioningLabel, " +
            "p.id AS partitioningId, " +
            "p.opening_date AS partitioningOpeningDate, " +
            "p.closing_date AS partitioningClosingDate, " +
            "p.return_date AS partitioningReturnDate, " +
            "su.identification_code AS surveyUnitIdentificationCode, " +
            "su.identification_name AS surveyUnitIdentificationName, " +
            "su.id_su AS surveyUnitId," +
            "c.datacollection_target AS dataCollectionTarget, " +
            "c.operation_upload_reference AS operationUploadReference, " +
            "q.model_name AS modelName " +
            "FROM questioning_accreditation qa " +
            "JOIN questioning q ON qa.questioning_id = q.id " +
            "JOIN partitioning p ON q.id_partitioning = p.id " +
            "JOIN campaign c ON p.campaign_id = c.id " +
            "JOIN survey s ON c.survey_id = s.id " +
            "JOIN source so ON s.source_id = so.id " +
            "JOIN survey_unit su ON q.survey_unit_id_su = su.id_su " +
            "WHERE qa.id_contact = :idec " +
            "LIMIT 500", nativeQuery = true)
    List<MyQuestionnaireDetailsDto> findQuestionnaireDetailsByIdec(String idec);

    Optional<QuestioningAccreditation> findAccreditationsByQuestioningIdAndIsMainTrue(UUID questioningId);

    Optional<QuestioningAccreditation> findAccreditationsByQuestioningIdAndIdContactAndIsMainFalse(UUID questioningId, String idContact);

    Optional<QuestioningAccreditation> findByQuestioning_IdAndIdContact(UUID questioningId, String idContact);
}
