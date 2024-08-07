package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitCommentRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitCommentServiceImpl implements SurveyUnitCommentService {

    private final SurveyUnitCommentRepository surveyUnitCommentRepository;
    @Override
    public SurveyUnitComment saveSurveyUnitComment(SurveyUnitComment surveyUnitComment) {
        return surveyUnitCommentRepository.save(surveyUnitComment);
    }
}
