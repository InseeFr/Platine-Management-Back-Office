package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceDto {

    private String id;
    private String longWording;
    private String shortWording;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys;
    private String logo;
    private String storageTime;
    private String personalData;

}
