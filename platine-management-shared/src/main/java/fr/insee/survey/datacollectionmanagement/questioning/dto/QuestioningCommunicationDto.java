package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QuestioningCommunicationDto {

    private Long id;
    private Long questioningId;
    private Date date;
    private String type;
    private String status;

}
