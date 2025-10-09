package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import java.util.Date;
import java.util.UUID;

public record QuestioningCommunicationInputDto(
    UUID questioningId,
    Date date,
    StatusCommunication status
) {}
