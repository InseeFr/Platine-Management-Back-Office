package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("InformationsCollecte")
@Getter
@Setter
public class BusinessMetadataDto {

    @JsonProperty("ServiceProducteur")
    private OwnerBusinessDto ownerBusinessDto;
    @JsonProperty("Enquete")
    private SurveyBusinessDto surveyBusinessDto;
    @JsonProperty("Campagne")
    private CampaignBusinessDto campaignBusinessDto;


}
