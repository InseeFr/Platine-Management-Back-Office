package fr.insee.survey.datacollectionmanagement.contact.dto;

import fr.insee.survey.datacollectionmanagement.contact.validation.ContactEventTypeValid;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

import java.util.Date;

@Getter
@Setter
public class ContactEventDto {

    private String identifier;
    private Date eventDate;
    @ContactEventTypeValid
    private String type;
    private JsonNode payload;
}
