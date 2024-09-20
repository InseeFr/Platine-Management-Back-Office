package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class AccreditationDetailDto {

    private String sourceId;
    private String surveyId;
    private String sourceWording;
    private int year;
    private PeriodEnum period;
    private String campaignId;
    private String partition;
    private Date partioningClosingDate;
    private String surveyUnitId;
    private String identificationName;
    private boolean isMain;
    private TypeQuestioningEvent lastEvent;
    private String questioningId;
    private String questioningUrl;


}
