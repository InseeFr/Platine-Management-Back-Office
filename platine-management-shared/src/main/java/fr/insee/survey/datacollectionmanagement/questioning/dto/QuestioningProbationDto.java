package fr.insee.survey.datacollectionmanagement.questioning.dto;

import java.util.UUID;

public record QuestioningProbationDto(UUID questioningId, boolean isOnProbation) {
}
