package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuestioningDetailsDto {
    private Long questioningId;
    private String campaignId;
    private List<String> listContactIdentifiers;
    private String surveyUnitId;
    private String surveyUnitIdentificationCode;
    private List<QuestioningEventDto> listEvents;
    private String lastEvent;
    private Date dateLastEvent;
    private List<QuestioningCommunicationDto> listCommunications;
    private String lastCommunication;
    private Date dateLastCommunication;
    private List<QuestioningCommentOutputDto> listQuestioningCommentOutputDto;
    private Date validationDate;
    private String readOnlyUrl;
}
