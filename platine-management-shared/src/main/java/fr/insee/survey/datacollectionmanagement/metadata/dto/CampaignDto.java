package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.validation.DataCollectionTargetValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignDto {

    @NotBlank
    private String id;
    private String surveyId;
    private int year;
    private String campaignWording;
    private String period;
    private boolean sensitivity;
    @DataCollectionTargetValid
    private String dataCollectionTarget;
}
