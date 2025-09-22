package fr.insee.survey.datacollectionmanagement.jms.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.service.stub.InterrogationFakePublisher;
import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;
import fr.insee.survey.datacollectionmanagement.jms.handler.stub.InterrogationBatchFakeService;
import fr.insee.survey.datacollectionmanagement.jms.model.JMSOutputMessage;
import fr.insee.survey.datacollectionmanagement.jms.model.ResponseCode;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class InterrogationQueueConsumerTest {

    private InterrogationQueueConsumer consumer;
    private InterrogationFakePublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();
    private InterrogationBatchFakeService interrogationBatchFakeService;

    @Mock
    private Message commandMessage;
    @Mock
    private Session session;

    private String additionalFieldCommand = "";
    private String additionalFieldInterrogation = "";

    private final String interrogationId = "a1b2c3d4-e5f6-4789-abcd-112233445566";
    private final String surveyUnitId = "0f1e2d3c-4b5a-6978-9123-abcdefabcdef";
    private final String partitionId = "3d3f6a2b-8d4d-4d7a-9c0b-1a2b3c4d5e6f";
    private final String contactId = "e3f1b1c2-d3a4-45ef-9a10-b2c3d4e5f6a7";
    private final String correlationId = "c7f0a0b1-9d8c-4e7f-b6a5-1234567890ab";
    private final String replyTo = "queueResponse";

    /**
     * JSON de commande minimal aligné avec les nouveaux records:
     * - payload.partitionId
     * - payload.interrogationId
     * - payload.surveyUnitId
     * - payload.corporateName -> Interrogation.identificationName
     * - payload.displayName   -> Interrogation.identificationCode
     * - payload.address{...}  -> Address
     * - payload.contacts[]    -> Contact
     */
    private final String defaultBody = """
        {
          "_id": {"$oid": "651a2f9c4d3e2b1a0f9c8d7e"},
          "processInstanceID": "9f2c9f4a-5a2b-4f0e-9a6f-2c8f0c3a1d55",
          %s
          "inProgress": true,
          "payload": {
            "partitionId": "%s",
            %s
            "interrogationId": "%s",
            "surveyUnitId": "%s",
            "corporateName": "Société Exemple SA",
            "displayName": "12345678900011",
            "address": {
              "streetNumber": "10",
              "repetitionIndex": "",
              "streetType": "rue",
              "streetName": "de la République",
              "addressSupplement": "Bâtiment B",
              "cityName": "Paris",
              "zipCode": "75011",
              "cedexCode": "",
              "cedexName": "",
              "specialDistribution": "",
              "countryCode": "FR",
              "countryName": "France"
            },
            "contacts": [
              {
                "contactId": "%s",
                "gender": "MME",
                "firstName": "Claire",
                "lastName": "Martin",
                "function": "Responsable administratif",
                "businessName": "Société Exemple SA",
                "contactRank": 1,
                "email": "claire.martin@example.com",
                "webConnectionId": "WEB-PORTAL-USER-001"
              }
            ]
          },
          "CampaignID": "ECO-ENT-2025",
          "correlationID": "%s",
          "questionnaireID": "Q-ECO-ENT-2025-V1",
          "done": false,
          "dateCreation": "2025-10-01T13:45:30Z",
          "replyTo": "%s"
        }
        """;

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.US);
        MockitoAnnotations.openMocks(this);
        interrogationBatchFakeService = new InterrogationBatchFakeService();
        publisher = new InterrogationFakePublisher();
        consumer = new InterrogationQueueConsumer(mapper, publisher, interrogationBatchFakeService);
    }

    @Test
    @DisplayName("Should create interrogation when message is valid")
    void ok() throws JMSException {
        // Given
        String ok = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId,
                replyTo
        );
        when(commandMessage.getBody(String.class)).thenReturn(ok);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then : l'interrogation construite avec les nouveaux champs
        Interrogation interrogationBatchUsed = interrogationBatchFakeService.getInterrogationBatchUsed();
        assertThat(interrogationBatchUsed).isNotNull();
        assertThat(interrogationBatchUsed.id().toString()).isEqualTo(interrogationId);
        assertThat(interrogationBatchUsed.surveyUnitId().toString()).isEqualTo(surveyUnitId);
        assertThat(interrogationBatchUsed.partitionId().toString()).isEqualTo(partitionId);
        assertThat(interrogationBatchUsed.identificationName()).isEqualTo("Société Exemple SA");
        assertThat(interrogationBatchUsed.identificationCode()).isEqualTo("12345678900011");
        assertThat(interrogationBatchUsed.address().streetName()).isEqualTo("de la République");
        assertThat(interrogationBatchUsed.contacts()).hasSize(1);
        assertThat(interrogationBatchUsed.contacts().get(0).id().toString()).isEqualTo(contactId);
        assertThat(interrogationBatchUsed.contacts().get(0).identifier()).isEqualTo("WEB-PORTAL-USER-001");
        assertThat(interrogationBatchUsed.contacts().get(0).isMain()).isTrue();
        assertThat(interrogationBatchUsed.contacts().get(0).email()).isEqualTo("claire.martin@example.com");

        // Et la réponse JMS publiée
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Test
    @DisplayName("Should log error when additional field command")
    void ShouldLogErrorWhenAdditionalFieldCommand(CapturedOutput output) throws JMSException {
        // Given
        additionalFieldCommand = "\"newFieldCommand\": true,";
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId,
                replyTo
        );
        // When & Then
        checkInvalidMessageError(body, "IOException : Unrecognized field \"newFieldCommand\"", output);
    }

    @Test
    @DisplayName("Should log error when additional field interrogation")
    void ShouldLogErrorWhenAdditionalFieldInterrogation(CapturedOutput output) throws JMSException {
        // Given
        additionalFieldInterrogation = "\"newFieldInterrogation\": true,";
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId,
                replyTo
        );
        // When & Then
        checkInvalidMessageError(
                body,
                "$.payload.newFieldInterrogation: is not defined in the schema and the schema does not allow additional properties",
                output
        );
    }

    @Test
    @DisplayName("Should log error when no correlation id in command message")
    void shouldLogErrorWhenNoCorrelationId(CapturedOutput output) throws JMSException {
        // Given
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                null,
                replyTo
        );
        // When & Then
        checkInvalidMessageError(body, "PropertyException : Missing or null field : 'correlationID'", output);
    }

    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException {
        // Given
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId,
                null
        );
        // When & Then
        checkInvalidMessageError(body, "PropertyException : Missing or null field : 'replyTo'", output);
    }

    @Test
    @DisplayName("Should log error when invalid json in command message")
    void shouldLogErrorWhenInvalidJsonMessageCommandMessage(CapturedOutput output) throws JMSException {
        // Given (interrogationId mal échappé)
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId + "\\",
                surveyUnitId,
                contactId,
                correlationId,
                replyTo
        );
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).contains("IOException : Illegal unquoted character");
    }

    @Test
    @DisplayName("Should log error when invalid json in interrogation")
    void shouldLogErrorWhenInvalidJsonMessageInterrogation(CapturedOutput output) throws JMSException {
        // Given (partitionId mal échappé via champ questionnaireID ici non utilisé mais garde le même principe d'invalidité)
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId + "\\",
                replyTo
        );
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).containsAnyOf(
                "IOException"
        );
    }

    @Disabled
    @Test
    @DisplayName("Should log error when jms exception")
    void shouldLogErrorWhenJMSException(CapturedOutput output) throws JMSException {
        // Given
        String exceptionMessage = "jms exception";
        when(commandMessage.getBody(String.class)).thenThrow(new JMSException(exceptionMessage));

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).contains(exceptionMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when survey unit id is invalid")
    void shouldLogErrorWhenInvalidSurveyUnitId() throws JMSException {
        // Given (surveyUnitId null)
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                null,
                contactId,
                correlationId,
                replyTo
        );
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String correlationPublisherId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();

        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
    }

    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendBusinessErrorWhenSurveyUnitCommandException() throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowInterrogationBatchException(true);
        String body = String.format(
                defaultBody,
                additionalFieldCommand,
                partitionId,
                additionalFieldInterrogation,
                interrogationId,
                surveyUnitId,
                contactId,
                correlationId,
                replyTo
        );
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        String expectedLogMessage = String.format(invalidPropertyName);
        assertThat(output).contains(expectedLogMessage);
    }
}
