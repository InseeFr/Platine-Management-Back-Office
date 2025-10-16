package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventRequestDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventResponseDto;

import java.util.List;

public interface SurveyUnitEventService {
    List<SurveyUnitEventResponseDto> getEvents(String surveyUnitId);

    void createEvent(SurveyUnitEventRequestDto eventDto, String surveyUnitId);
}
