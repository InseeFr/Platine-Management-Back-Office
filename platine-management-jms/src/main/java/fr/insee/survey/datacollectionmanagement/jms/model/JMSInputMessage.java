package fr.insee.survey.datacollectionmanagement.jms.model;

import fr.insee.modelefiliere.InterrogationDto;

public record JMSInputMessage(
        String correlationID,
        String replyTo,
        InterrogationDto payload
) {
}
