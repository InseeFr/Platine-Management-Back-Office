package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class SurveyUnitPartitioningDto {

    private String sourceWording;
    private int year;
    private PeriodEnum period;
    private String campaignWording;
        private Instant partioningClosingDate;
    private TypeQuestioningEvent lastEvent;

}
