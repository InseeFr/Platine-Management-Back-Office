package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface QuestioningRepository extends JpaRepository<Questioning, UUID> {

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Integer deleteByidPartitioning(String idPartitioning);

    Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                                String surveyUnitIdSu);

    @Query("""
                SELECT
                    q
                FROM
                    Questioning q
                WHERE
                    q.surveyUnit.idSu = :surveyUnitId
                    AND q.idPartitioning in (
                        SELECT
                            p.id
                        FROM
                            Partitioning p
                        WHERE
                            p.campaign.id =:campaignId)
            """)
    List<Questioning> findQuestioningByCampaignIdAndSurveyUnitId(String campaignId, String surveyUnitId);

    /**
     * Retrieves the necessary data for a CSV export of questionings for a given campaign.
     * This query uses a JOIN for performance and a constructor projection to create DTOs
     * directly, which is highly efficient.
     *
     * @param campaignId The ID of the campaign.
     * @return A list of DTOs ready for CSV conversion.
     */
    @Query("""
    SELECT new fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto(
        q.id,
        q.idPartitioning,
        q.surveyUnit.idSu,
        q.highestEventType,
        q.highestEventDate,
        q.isOnProbation
    )
    FROM Questioning q
    JOIN Partitioning p ON p.id = q.idPartitioning
    WHERE p.campaign.id = :campaignId
    """)
    List<QuestioningCsvDto> findQuestioningDataForCsvByCampaignId(String campaignId);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    boolean existsBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

    @Query("select distinct q.id from Questioning q where q.id in :ids")
    Set<UUID> findExistingInterrogationIds(Collection<UUID> ids);
}
