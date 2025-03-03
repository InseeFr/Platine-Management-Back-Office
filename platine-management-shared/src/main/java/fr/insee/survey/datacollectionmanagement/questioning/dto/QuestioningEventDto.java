package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.questioning.validation.QuestioningEventTypeValid;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestioningEventDto {

    private Long id;
    private Long questioningId;
    private Date eventDate;
    @QuestioningEventTypeValid
    private String type;
    private JsonNode payload;

}
