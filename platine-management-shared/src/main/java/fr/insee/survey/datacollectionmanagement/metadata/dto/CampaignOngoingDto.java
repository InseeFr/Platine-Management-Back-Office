package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignOngoingDto {

    @NotBlank
    private String id   ;
    private String sourceId;
    private int year;
    private int yearCollect;
    private String period;
    private String periodCollect;
    private String dataCollectionTarget;
    private boolean sensitivity;
}