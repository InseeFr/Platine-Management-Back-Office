package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class SurveyServiceStub implements SurveyService {
    @Override
    public Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity) {
        return null;
    }

    @Override
    public Survey findById(String id) {
        return null;
    }

    @Override
    public Optional<Survey> findOptionalById(String id) {
        return Optional.empty();
    }

    @Override
    public Page<Survey> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Survey insertOrUpdateSurvey(Survey survey) {
        return null;
    }

    @Override
    public void deleteSurveyById(String id) {

    }

    @Override
    public boolean isSurveyOngoing(String id) {
        return "ONGOING".equals(id);
    }
}
