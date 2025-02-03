package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitCommentRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitCommentService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitCommentServiceImpl implements SurveyUnitCommentService {

    private final SurveyUnitCommentRepository surveyUnitCommentRepository;
    private final SurveyUnitService surveyUnitService;
    private final ModelMapper modelMapper;
    @Override
    public SurveyUnitCommentOutputDto saveSurveyUnitComment(SurveyUnit surveyUnit, SurveyUnitCommentInputDto surveyUnitCommentInputDto) {

        SurveyUnitComment surveyUnitComment = convertToEntity(surveyUnitCommentInputDto);
        surveyUnitComment.setDate(new Date());
        surveyUnitComment.setSurveyUnit(surveyUnit);
        SurveyUnitComment newSurveyUnitComment = surveyUnitCommentRepository.save(surveyUnitComment);
        return convertToOutputDto(newSurveyUnitComment);
    }

    public SurveyUnitComment convertToEntity(SurveyUnitCommentInputDto surveyUnitCommentDto) {
        return modelMapper.map(surveyUnitCommentDto, SurveyUnitComment.class);
    }

    public SurveyUnitCommentOutputDto convertToOutputDto(SurveyUnitComment surveyUnitComment) {
        return modelMapper.map(surveyUnitComment, SurveyUnitCommentOutputDto.class);
    }
}
