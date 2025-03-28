package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyCreateContextDto {

    @NotBlank
    private String id;
    private String technicalId;
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
    private boolean compulsoryNature;
    private String rgpdBlock;
    private String sendPaperQuestionnaire;
    private boolean reExpedition;
    private String managementApplicationName;
    private boolean contactExtraction;
    private String contactExtractionNb;
    private String surveyStatus;
    private boolean sviUse;
    private String sviNumber;
}
