package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCommonsOngoingDto {

    @NotBlank
    private String id;
    private String dataCollectionTarget;
    private boolean sensitivity;
    private String collectMode;

}