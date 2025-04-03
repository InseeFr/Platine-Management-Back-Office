package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class CampaignBusinessDto {

    @JsonProperty("Libelle")
    private String campaignWording;
}
