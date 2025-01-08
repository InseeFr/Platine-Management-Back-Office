package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.metadata.validation.ParameterEnumValid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParamsDto {
    @ParameterEnumValid
    private String paramId;
    private String paramValue;
}
