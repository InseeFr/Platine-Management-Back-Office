package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceCreateDto {
    private String id;
    private String technicalId;
    private String longWording;
    private String shortWording;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys;
}
