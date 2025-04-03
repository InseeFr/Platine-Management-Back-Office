package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class OwnerBusinessDto {

    @JsonProperty("Article")
    private String determiner;
    @JsonProperty("Libelle")
    private  String label;
    @JsonProperty("MinistereTutelle")
    private  String ministry;

}
