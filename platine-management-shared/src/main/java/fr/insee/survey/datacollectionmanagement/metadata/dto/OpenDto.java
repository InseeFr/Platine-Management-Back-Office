package fr.insee.survey.datacollectionmanagement.metadata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenDto {
    private boolean opened;
    private boolean forceClose;
    private String messageSurveyOffline;
    private String messageInfoSurveyOffline;

}
