package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventSource;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventType;

public record SurveyUnitEventResponseDto(
        String campaignId,
        SurveyUnitEventType eventType,
        SurveyUnitEventSource source,
        Long eventDate,
        Long eventCreationDate
) {
}
