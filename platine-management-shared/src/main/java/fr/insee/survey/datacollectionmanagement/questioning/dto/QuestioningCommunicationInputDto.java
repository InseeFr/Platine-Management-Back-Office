package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record QuestioningCommunicationInputDto(
    @NotNull(message = "The questioningId must not be null")
    UUID questioningId,
    @NotNull(message = "The status must not be null")
    StatusCommunication status,
    @NotNull(message = "The communicationType must not be null")
    TypeCommunicationEvent communicationType
) {}
