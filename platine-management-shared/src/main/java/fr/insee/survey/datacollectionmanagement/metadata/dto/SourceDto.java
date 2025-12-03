package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceDto {

    private String id;
    private String longWording;
    private String shortWording;
    private SourceTypeEnum type;
    private PeriodicityEnum periodicity;
    private boolean mandatoryMySurveys;
    private String logo;
    private String storageTime;
    private String personalData;
    private Boolean displayFaq;
    private boolean paperFormInputEnabled;

}
