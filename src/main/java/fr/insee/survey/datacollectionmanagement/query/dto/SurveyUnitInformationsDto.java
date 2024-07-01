package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SurveyUnit")
public class SurveyUnitInformationsDto {

    @JsonProperty("Label")
    private String label;

    @JsonProperty("IdSu")
    private String surveyUnitId;

    @JsonProperty("IdentificationName")
    private String identificationName;
}
