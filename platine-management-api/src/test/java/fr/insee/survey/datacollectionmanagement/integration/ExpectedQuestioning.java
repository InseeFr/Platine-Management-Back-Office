package fr.insee.survey.datacollectionmanagement.integration;

public record ExpectedQuestioning(
        Integer key,
        String surveyUnitId,
        String validationDate,
        String highestEventType,
        String lastCommunicationType
) {}
