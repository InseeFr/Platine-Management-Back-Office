package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import jakarta.validation.constraints.NotBlank;

public class CampaignContextDto {

    @NotBlank
    private String id;
    private String surveyId;
    private String surveyShortWording;
    private int year;
    private String shortWording;
    private String campaignWording;
    private PeriodEnum period;

}
