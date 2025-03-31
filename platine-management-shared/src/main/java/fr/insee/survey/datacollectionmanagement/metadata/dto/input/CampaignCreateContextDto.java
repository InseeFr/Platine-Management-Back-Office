package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.survey.datacollectionmanagement.metadata.validation.DataCollectionTargetValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CampaignCreateContextDto {

    @NotBlank
    private String id;
    private UUID technicalId;
    private String surveyId;
    private int year;
    private String campaignWording;
    private String period;
    private boolean sensitivity;
    @DataCollectionTargetValid
    private String dataCollectionTarget;
}
