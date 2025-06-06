package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;

import java.util.List;

public record SearchQuestioningParams(
        String userOrSurveyUnitId,
        List<String> campaignIds,
        List<TypeQuestioningEvent> typeQuestioningEvents,
        List<TypeCommunicationEvent> typeCommunicationEvents) {
}