package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyUnitEventRepository extends JpaRepository<SurveyUnitEvent, Long> {

    List<SurveyUnitEvent> findBySurveyUnitIdSuOrderByDateDesc(String surveyUnitId);
}
