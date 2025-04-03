package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CampaignBusinessDto {

    @JsonProperty("Libelle")
    private String campaignWording;
}
