package fr.insee.survey.datacollectionmanagement.metadata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyDto {

    @NotBlank
    private String id;
    @NotBlank
    private String sourceId;
    private Integer year;
    private Integer sampleSize;
    private String longWording;
    private String shortWording;
    private String shortObjectives;
    private String longObjectives;
    private String visaNumber;
    private String cnisUrl;
    private String diffusionUrl;
    private String noticeUrl;
    private String specimenUrl;
    private String communication;
}
