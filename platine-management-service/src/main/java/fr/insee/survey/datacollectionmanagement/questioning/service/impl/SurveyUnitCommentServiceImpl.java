package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitCommentRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitCommentServiceImpl implements SurveyUnitCommentService {

    private final SurveyUnitCommentRepository surveyUnitCommentRepository;
    private final ModelMapper modelMapper;
    @Override
    public SurveyUnitComment saveSurveyUnitComment(SurveyUnitComment surveyUnitComment) {
        return surveyUnitCommentRepository.save(surveyUnitComment);
    }

    @Override
    public SurveyUnitComment convertToEntity(SurveyUnitCommentInputDto surveyUnitCommentDto) {
        return modelMapper.map(surveyUnitCommentDto, SurveyUnitComment.class);
    }

    @Override
    public SurveyUnitCommentOutputDto convertToOutputDto(SurveyUnit surveyUnit) {
        return modelMapper.map(surveyUnit, SurveyUnitCommentOutputDto.class);
    }
}
