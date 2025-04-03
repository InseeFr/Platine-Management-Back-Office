package fr.insee.survey.datacollectionmanagement.metadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyBusinessDto {


    @JsonProperty("AnneeCollecte")
    private String year;
    @JsonProperty("CaractereObligatoire")
    private String compulsaryNature;
    @JsonProperty("ObjectifsCourts")
    private String shortObjectives;
    @JsonProperty("StatutEnquete")
    private String surveyStatus;
    @JsonProperty("NumeroVisa")
    private String visaNumber;
    @JsonProperty("URLDiffusion")
    private String diffusionUrl;
    @JsonProperty("URLNotice")
    private String noticeUrl;
    @JsonProperty("Article")
    private String determiner;
    @JsonProperty("URLSpecimen")
    private String specimenUrl;

}
