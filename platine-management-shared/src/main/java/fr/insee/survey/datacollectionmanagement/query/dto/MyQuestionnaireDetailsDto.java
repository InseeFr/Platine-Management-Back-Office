package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyQuestionnaireDetailsDto {
    private String sourceId;
    private Integer surveyYear;
    private String periodCollect;
    private UUID questioningId;
    private String partitioningLabel;
    private String partitioningId;
    private Date partitioningOpeningDate;
    private Date partitioningClosingDate;
    private Date partitioningReturnDate;
    private String surveyUnitIdentificationCode;
    private String surveyUnitIdentificationName;
    private String surveyUnitId;
    private String dataCollectionTarget;
    private String operationUploadReference;
    private String modelName;
}
