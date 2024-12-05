package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    WHERE q.surveyUnit.idSu =:searchParam 
        OR q.surveyUnit.identificationName =:searchParam 
        OR acc.idContact =:searchParam
    """)

    Page<Questioning> findQuestioningByParam(String searchParam,Pageable pageable );

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

}
