package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyQuestionnaireDetailsDto {
    private String sourceId;
    private Long questioningId;
    private String partitioningLabel;
    private String partitioningId;
    private String surveyUnitIdentificationCode;
    private String surveyUnitIdentificationName;
    private String surveyUnitId;
    private DataCollectionEnum dataCollectionTarget;
}
