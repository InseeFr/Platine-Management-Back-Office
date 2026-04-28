package fr.insee.survey.datacollectionmanagement.contact.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.validation.ContactEventTypeValid;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ContactEventDto {

    private String identifier;
    private Instant eventDate;
    @ContactEventTypeValid
    private String type;
    private JsonNode payload;
}
