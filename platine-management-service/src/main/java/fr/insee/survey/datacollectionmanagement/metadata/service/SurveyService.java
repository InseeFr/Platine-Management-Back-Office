package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SurveyService {

    Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity);

    Survey findById(String id);

    Optional<Survey> findOptionalById(String id);

    Page<Survey> findAll(Pageable pageable);

    Survey insertOrUpdateSurvey(Survey survey);

    void deleteSurveyById(String id);

}
