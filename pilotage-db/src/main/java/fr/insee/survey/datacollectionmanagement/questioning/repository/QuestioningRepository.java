package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface QuestioningRepository extends JpaRepository<Questioning, Long> {

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning,
            String surveyUnitIdSu);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);
}
