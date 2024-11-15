package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceOnlineStatusDto {

    @NotBlank
    private String id;
    private String longWording;
    private String shortWording;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys = false;
    private boolean forceClose = false;
    private String messageInfoSurveyOffline = "";
    private String messageSurveyOffline = "";
    private String ownerId;
    private String supportId;

}
