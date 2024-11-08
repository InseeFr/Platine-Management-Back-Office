package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Indicates whether or not you need to use the my surveys portal", defaultValue = "false")
    private boolean mandatoryMySurveys = false;
    @Schema(description = "Indicates if the source should be force closed", defaultValue = "false")
    private boolean forceClose = false;
    private String messageInfoSurveyOffline = "";
    private String messageSurveyOffline = "";
    private String ownerId;
    private String supportId;

}
