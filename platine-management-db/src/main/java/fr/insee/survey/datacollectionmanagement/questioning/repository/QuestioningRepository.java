package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface QuestioningRepository extends JpaRepository<Questioning, Long> {

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                      String surveyUnitIdSu);

    @Query("""
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                    JOIN FETCH q.surveyUnit su
                WHERE UPPER(q.surveyUnit.idSu) = :searchParam
                UNION
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                    JOIN FETCH  q.surveyUnit su
                WHERE UPPER(q.surveyUnit.identificationName) = :searchParam
                UNION
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                    JOIN FETCH q.surveyUnit su
                WHERE UPPER(q.surveyUnit.identificationCode) = :searchParam
                UNION
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                    JOIN FETCH q.surveyUnit su
                WHERE EXISTS (
                    SELECT 1 FROM QuestioningAccreditation qa
                    WHERE qa.questioning = q
                    AND UPPER(qa.idContact) = :searchParam)
            """)
    List<Questioning> findQuestioningByParam(String searchParam);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

    @Query("""
                SELECT q.id FROM Questioning q
            """)
    Page<Long> findQuestioningIds(Pageable pageable);

    @Query("""
                SELECT q FROM Questioning q
                    LEFT JOIN FETCH q.questioningAccreditations acc
                    LEFT JOIN FETCH q.questioningEvents evt
                    LEFT JOIN FETCH q.questioningCommunications comm
                WHERE q.id IN :ids
            """)
    List<Questioning> findQuestioningsByIds(@Param("ids") List<Long> ids);

}
