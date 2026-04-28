package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;

import java.time.Instant;
import java.util.UUID;

public record QuestioningCsvDto(
        UUID interrogationId,
        String partitioningId,
        String surveyUnitId,
        TypeQuestioningEvent highestEventType,
                Instant highestEventDate,
        boolean isOnProbation
) {}
