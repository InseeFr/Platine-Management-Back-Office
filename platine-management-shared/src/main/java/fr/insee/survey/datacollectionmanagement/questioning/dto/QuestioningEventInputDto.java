package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusEvent;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class QuestioningEventInputDto {

    private UUID questioningId;
    private Date date;
    private JsonNode payload;
    private StatusEvent status;


}
