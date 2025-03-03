package fr.insee.survey.datacollectionmanagement.questioning.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestioningEventInputDto {

    private Long questioningId;
    private Date date;
    private JsonNode payload;

}
