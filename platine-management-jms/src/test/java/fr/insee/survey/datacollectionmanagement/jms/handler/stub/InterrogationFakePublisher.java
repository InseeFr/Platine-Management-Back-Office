package fr.insee.survey.datacollectionmanagement.jms.handler.stub;


import fr.insee.survey.datacollectionmanagement.jms.handler.InterrogationResponsePublisher;
import fr.insee.survey.datacollectionmanagement.jms.model.JMSOutputMessage;
import lombok.Getter;

public class InterrogationFakePublisher implements InterrogationResponsePublisher {

    @Getter
    private String replyQueueUsed = null;

    @Getter
    private String correlationIdUsed = null;

    @Getter
    private JMSOutputMessage responseSent = null;

    @Override
    public void send(String replyQueue, String correlationId, JMSOutputMessage responseMessage) {
        replyQueueUsed = replyQueue;
        correlationIdUsed = correlationId;
        responseSent = responseMessage;
    }
}
