package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class QuestioningWebclientDto {

    @NotBlank
    private String idPartitioning;
    @NotBlank
    private String modelName;
    private SurveyUnitDto surveyUnit;
    private List<ContactAccreditationDto> contacts;

}
