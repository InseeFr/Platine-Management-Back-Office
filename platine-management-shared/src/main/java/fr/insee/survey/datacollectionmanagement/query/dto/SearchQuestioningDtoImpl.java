package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SearchQuestioningDtoImpl implements SearchQuestioningDto {

    private UUID questioningId;
    private String campaignId;
    private List<String> listContactIdentifiers;
    private String surveyUnitId;
    private String surveyUnitIdentificationCode;
    private String lastEvent;
    private String lastCommunication;
    private Date validationDate;


}
