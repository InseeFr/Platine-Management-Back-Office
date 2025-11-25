package fr.insee.survey.datacollectionmanagement.questioning;

import java.util.UUID;

public record InterrogationPriorityInputDto(UUID interrogationId, Long priority) {
}
