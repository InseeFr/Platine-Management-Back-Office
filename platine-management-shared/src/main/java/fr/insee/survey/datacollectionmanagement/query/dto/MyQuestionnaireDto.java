package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionaireStatusTypeEnum;
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
    private String deliveryUrl;
    private Long questioningId;
    private String partitioningLabel;
}
