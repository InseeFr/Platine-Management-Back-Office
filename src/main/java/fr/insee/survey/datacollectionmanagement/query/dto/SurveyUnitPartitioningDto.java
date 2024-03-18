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
public class SurveyUnitPartitioningDto {

    private String sourceWording;
    private int year;
    private PeriodEnum period;
    private String campaignWording;
    private Date partioningClosingDate;
    private TypeQuestioningEvent lastEvent;

}
