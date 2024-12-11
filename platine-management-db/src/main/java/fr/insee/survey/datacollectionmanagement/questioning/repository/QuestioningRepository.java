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

    /*@Query(value = """
                    (select
                                q1_0.*
                            from
                                questioning q1_0
                            where
                                q1_0.survey_unit_id_su=:searchParam
                            union
                            select
                                q2_0.*
                            from
                                questioning q2_0
                            join
                                survey_unit su2_0
                                    on su2_0.id_su=q2_0.survey_unit_id_su
                            where
                                su2_0.identification_name=:searchParam
                            union
                            select
                                q3_0.*
                            from
                                questioning q3_0
                            left join
                                questioning_accreditation qa3_0
                                    on q3_0.id=qa3_0.questioning_id
                            where
                                exists(select
                                    1
                                from
                                    questioning_accreditation qa4_0
                                where
                                    qa4_0.questioning_id=q3_0.id)
                                and qa3_0.id_contact=:searchParam)
    """, nativeQuery = true)*/
    @Query("""
    SELECT q FROM Questioning q
        LEFT JOIN FETCH q.questioningAccreditations acc
        LEFT JOIN FETCH q.questioningEvents evt
        LEFT JOIN FETCH q.questioningCommunications comm
    WHERE q.surveyUnit.idSu = :searchParam
    UNION
    SELECT q FROM Questioning q
        LEFT JOIN FETCH q.questioningAccreditations acc
        LEFT JOIN FETCH q.questioningEvents evt
        LEFT JOIN FETCH q.questioningCommunications comm
    WHERE q.surveyUnit.identificationName = :searchParam
    UNION
    SELECT q FROM Questioning q
        LEFT JOIN FETCH q.questioningAccreditations acc
        LEFT JOIN FETCH q.questioningEvents evt
        LEFT JOIN FETCH q.questioningCommunications comm
    WHERE EXISTS (
        SELECT 1 FROM QuestioningAccreditation qa
        WHERE qa.questioning = q
    ) AND acc.idContact = :searchParam
""")
    Page<Questioning> findQuestioningByParam(String searchParam, Pageable pageable);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

}
