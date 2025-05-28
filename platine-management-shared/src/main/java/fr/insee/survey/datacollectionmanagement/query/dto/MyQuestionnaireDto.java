package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class MyQuestionnaireDto {

    private String sourceId;
    private String surveyUnitIdentificationCode;
    private String surveyUnitIdentificationName;
    private String questioningStatus;
    private String questioningAccessUrl;
    private String depositProofUrl;
    private UUID questioningId;
    private String partitioningLabel;
    private String partitioningId;
    private Instant partitioningReturnDate;
    private String surveyUnitId;
}
