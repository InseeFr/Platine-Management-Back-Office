package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyQuestionnaireDetailsDto {
    private String sourceId;
    private Long questioningId;
    private String partitioningLabel;
    private Date partitioningClosingDate;
    private String partitioningId;
    private String surveyUnitIdentificationCode;
    private String surveyUnitIdentificationName;
    private String surveyUnitId;
    private String dataCollectionTarget;
}
