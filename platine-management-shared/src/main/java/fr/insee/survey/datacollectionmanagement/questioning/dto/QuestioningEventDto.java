package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.questioning.validation.QuestioningEventTypeValid;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class QuestioningEventDto {

    private Long id;
    private UUID questioningId;
    private Date eventDate;
    @QuestioningEventTypeValid
    private String type;
    private JsonNode payload;

    @Override
    public String toString() {
        return "QuestioningEventDto{" +
                "id=" + id +
                ", questioningId=" + questioningId +
                ", eventDate=" + eventDate +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
