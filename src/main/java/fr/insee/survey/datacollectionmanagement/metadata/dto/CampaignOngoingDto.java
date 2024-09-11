package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignOngoingDto {

    @NotBlank
    private String id;
    private String sourceId;
    private int year;
    private String period;
}
