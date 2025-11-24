package fr.insee.survey.datacollectionmanagement.jms.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.insee.modelefiliere.AddressDto;
import fr.insee.modelefiliere.CommandDto;
import fr.insee.modelefiliere.InterrogationDto;
import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;
import fr.insee.survey.datacollectionmanagement.jms.model.JMSOutputMessage;
import fr.insee.survey.datacollectionmanagement.jms.model.ResponseCode;
import fr.insee.survey.datacollectionmanagement.jms.service.exception.PropertyException;
import fr.insee.survey.datacollectionmanagement.jms.service.validator.PropertyValidator;
import fr.insee.survey.datacollectionmanagement.jms.validation.JsonSchemaValidator;
import fr.insee.survey.datacollectionmanagement.jms.validation.SchemaType;
import fr.insee.survey.datacollectionmanagement.jms.validation.SchemaValidationException;
import fr.insee.survey.datacollectionmanagement.metadata.service.InterrogationBatchException;
import fr.insee.survey.datacollectionmanagement.metadata.service.InterrogationBatchService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final ObjectMapper objectMapper;
    private final InterrogationResponsePublisher replyQueuePublisher;
    private final InterrogationBatchService interrogationBatchService;

    @JmsListener(destination = "${fr.insee.broker.queue.interrogation.request}")
    public void createInterrogation(Message message, Session session) throws JMSException {
        String replyQueue=null;
        String correlationId=null;
        JMSOutputMessage responseMessage;
        try {
            String json = message.getBody(String.class);
            // jakarta.jms.JMSException: Invalid JSON: Java 8 date/time type java.time.Instant not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (or disable MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES)
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // ISO-8601
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            // ---
            JsonNode root = objectMapper.readTree(json);

            replyQueue = PropertyValidator.textValue(root, "replyTo");
            correlationId = PropertyValidator.textValue(root, "correlationID");

            CommandDto command = JsonSchemaValidator.readAndValidateFromClasspath(
                    root,
                    SchemaType.PROCESS_MESSAGE.getSchemaFileName(),
                    CommandDto.class,
                    objectMapper
            );
            log.debug(command.toString());

            InterrogationDto interrogationFiliere = new InterrogationDto()
                    .partitionId(UUID.fromString(root.path("payload").path("partitionId").asText()))
                    .interrogationId(UUID.fromString(root.path("payload").path("interrogationId").asText()))
                    .displayName("Interrogation simple")
                    .cityCode("34172");
            AddressDto addressDto = new AddressDto();
            addressDto.setStreetNumber(root
                    .path("interrogation")
                    .path("streetNumber")
                    .asText());
            interrogationFiliere.setAddress(addressDto);
            Interrogation interrogation = Interrogation.fromFiliereInterrogation(interrogationFiliere);
            interrogationBatchService.saveInterrogation(interrogation);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);

        } catch (InterrogationBatchException ibe) {
            log.error("InterrogationBatchException : {}", ibe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ibe.getMessage());
        } catch (SchemaValidationException jsv) {
            log.error("JsonSchemaValidator : {}", jsv.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, jsv.getMessage());
        } catch (IOException ioe) {
            log.error("IOException : {}", ioe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ioe.getMessage());
        }catch (EntityNotFoundException enfe) {
            log.error("EntityNotFoundException : {}", enfe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.NOT_FOUND, enfe.getMessage());
        } catch (PropertyException pe) {
            log.error("PropertyException : {}", pe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, pe.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}