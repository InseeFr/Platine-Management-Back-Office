package fr.insee.survey.datacollectionmanagement.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssistanceDto {
    private String mailAssistance;
    private String surveyUnitId;
}
