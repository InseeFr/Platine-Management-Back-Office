package fr.insee.survey.datacollectionmanagement.questioning.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestioningCommunicationDto {

    private Long id;
    private UUID questioningId;
    private Instant date;
    private String type;
    private boolean withQuestionnaire;
    private boolean withReceipt;
    private String status;

}
