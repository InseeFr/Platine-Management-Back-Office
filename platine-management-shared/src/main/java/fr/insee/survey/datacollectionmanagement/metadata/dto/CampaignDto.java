package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CampaignDto {

    @NotBlank
    private UUID id;
    private String surveyId;
    private String surveyShortWording;
    private int year;
    private String shortWording;
    private String campaignWording;
    private PeriodEnum period;
}
