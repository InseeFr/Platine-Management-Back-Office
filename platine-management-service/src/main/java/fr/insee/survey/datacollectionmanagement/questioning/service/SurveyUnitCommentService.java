package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitCommentOutputDto;

public interface SurveyUnitCommentService {

    SurveyUnitComment saveSurveyUnitComment(SurveyUnitComment surveyUnitComment);

    SurveyUnitComment convertToEntity(SurveyUnitCommentInputDto surveyUnitCommentDto);

    SurveyUnitCommentOutputDto convertToOutputDto(SurveyUnit surveyUnit);


}
