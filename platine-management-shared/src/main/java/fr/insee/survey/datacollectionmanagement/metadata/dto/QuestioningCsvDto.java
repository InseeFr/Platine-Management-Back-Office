package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import java.util.UUID;

import java.util.Date;

public record QuestioningCsvDto(
        UUID interrogationId,
        String partitioningId,
        String surveyUnitId,
        TypeQuestioningEvent highestEventType,
        Date highestEventDate,
        boolean isOnProbation
) {}