package fr.insee.survey.datacollectionmanagement.jms.handler;

import fr.insee.survey.datacollectionmanagement.jms.model.JMSOutputMessage;

public interface InterrogationResponsePublisher {
    void send(String replyQueue, String correlationId, JMSOutputMessage responseMessage);
}
