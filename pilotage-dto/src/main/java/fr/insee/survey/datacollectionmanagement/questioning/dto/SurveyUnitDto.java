package fr.insee.survey.datacollectionmanagement.questioning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyUnitDto {

    @NotBlank
    private String idSu;
    private String identificationCode;
    private String identificationName;
    private SurveyUnitAddressDto address;
}
