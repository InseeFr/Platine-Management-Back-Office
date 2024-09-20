package fr.insee.survey.datacollectionmanagement.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("QuestioningInformations")
public class QuestioningInformationsDto {

    @JsonProperty("ReturnDate")
    private String returnDate;

    @JsonProperty("Logo")
    private String logo;

    @JsonProperty("UrlLogout")
    private String urlLogout;

    @JsonProperty("UrlAssistance")
    private String urlAssistance;

    @JsonProperty("Contact")
    private ContactInformationsDto  contactInformationsDto;

    @JsonProperty("SurveyUnit")
    private SurveyUnitInformationsDto  surveyUnitInformationsDto;
}
