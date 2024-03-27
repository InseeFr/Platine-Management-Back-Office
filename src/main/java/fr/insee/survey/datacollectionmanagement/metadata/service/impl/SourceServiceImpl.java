package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SourceRepository sourceRepository;

    public Source findById(String source) {
        return sourceRepository.findById(source).orElseThrow(() -> new NotFoundException(String.format("Source %s not found", source)));
    }

    @Override
    public Page<Source> findAll(Pageable pageable) {
        return sourceRepository.findAll(pageable);
    }

    @Override
    public Source insertOrUpdateSource(Source source) {
        try {
            Source sourceBase = findById(source.getId());
            log.info("Update source with the id {}", source.getId());
            source.setSurveys(sourceBase.getSurveys());
        } catch (NotFoundException e) {
            log.info("Create source with the id {}", source.getId());
            return sourceRepository.save(source);

        }
        return sourceRepository.save(source);
    }

    @Override
    public void deleteSourceById(String id) {
        sourceRepository.deleteById(id);

    }

    @Override
    public Source addSurveyToSource(Source source, Survey survey) {
        Set<Survey> surveys;
        try {
            Source sourceBase = findById(source.getId());
            surveys = sourceBase.getSurveys();
            if (!isSurveyPresent(survey, sourceBase)) {
                surveys.add(survey);
            }
        } catch (NotFoundException e) {
            surveys = Set.of(survey);

        }
        source.setSurveys(surveys);
        return source;
    }

    private boolean isSurveyPresent(Survey su, Source s) {
        for (Survey survey : s.getSurveys()) {
            if (survey.getId().equals(su.getId())) {
                return true;
            }
        }
        return false;
    }

}
