package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface QuestioningRepository extends JpaRepository<Questioning, UUID> {

    Set<Questioning> findByIdPartitioning(String idPartitioning);

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

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    boolean existsBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

    @Query("""
                SELECT q.id FROM Questioning q
            """)
    Page<UUID> findQuestioningIds(Pageable pageable);

    @Query("""
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                WHERE q.id IN :ids
            """)
    List<Questioning> findQuestioningsByIds(@Param("ids") List<UUID> ids);

}
