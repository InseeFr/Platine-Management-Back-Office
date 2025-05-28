package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QuestioningDto {

    private UUID id;

    private String surveyUnitId;
    private String idPartitioning;
    private String modelName;

}
