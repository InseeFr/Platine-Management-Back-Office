package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceCompleteDto {

    @NotBlank
    private String id;
    private String longWording;
    private String shortWording;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys;
    private String ownerId;
    private String supportId;

}
