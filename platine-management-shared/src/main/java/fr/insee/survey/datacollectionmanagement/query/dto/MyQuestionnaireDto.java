package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MyQuestionnaireDto {

    private String sourceId;
    private String surveyUnitIdentificationCode;
    private String surveyUnitIdentificationName;
    private String questioningStatus;
    private String questioningAccessUrl;
    private String depositProofUrl;
    private Long questioningId;
    private String partitioningLabel;
    private String partitioningId;
    private Timestamp partitioningClosingDate;
    private String surveyUnitId;
}
