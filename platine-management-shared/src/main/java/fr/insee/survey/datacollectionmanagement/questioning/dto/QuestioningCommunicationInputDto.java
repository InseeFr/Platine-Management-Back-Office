package fr.insee.survey.datacollectionmanagement.questioning.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestioningCommunicationInputDto {

    private UUID questioningId;
    private Date date;
    private StatusCommunication status;

}
