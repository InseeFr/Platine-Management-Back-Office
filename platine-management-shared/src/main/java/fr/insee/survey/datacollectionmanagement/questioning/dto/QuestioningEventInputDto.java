package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class QuestioningEventInputDto {

    private UUID questioningId;
    private Date date;
    private JsonNode payload;
    StatusEvent status;


}
