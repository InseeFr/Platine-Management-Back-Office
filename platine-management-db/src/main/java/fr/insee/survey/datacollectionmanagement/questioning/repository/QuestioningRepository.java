package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface QuestioningRepository extends JpaRepository<Questioning, Long> {

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
                                                      String surveyUnitIdSu);

    Page<Questioning> findBySurveyUnitIdSuOrSurveyUnitIdentificationCodeOrQuestioningAccreditationsIdContact(
            String surveyUnitIdSu, String surveyUnitIdentificationCode, String idContact, Pageable pageable);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Page<Questioning> findAll(Pageable pageable);

}
