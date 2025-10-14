package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SurveyServiceStub implements SurveyService {

    private List<Survey> savedSurveys = new ArrayList<>();

    @Getter
    private Survey lastSaved;

    @Override
    public Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity) {
        return null;
    }

    @Override
    public Survey findById(String id) {
        return savedSurveys.stream()
                .filter(survey -> survey.getId().equals(id))
                .findFirst()
                .get();
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
        savedSurveys.removeIf(x -> Objects.equals(x.getId(), survey.getId()));
        savedSurveys.add(survey);
        lastSaved = survey;
        return survey;
    }

    @Override
    public void deleteSurveyById(String id) {

    }

    @Override
    public boolean isSurveyOngoing(String id) {
        return "ONGOING".equals(id);
    }

    public void setSavedSurveys(List<Survey> surveys) {
        savedSurveys = new ArrayList<>(surveys);
    }
}
