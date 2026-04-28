package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class QuestioningEventInputDto {

    private UUID questioningId;
    private Instant date;
    private JsonNode payload;
    private StatusEvent status;


}
