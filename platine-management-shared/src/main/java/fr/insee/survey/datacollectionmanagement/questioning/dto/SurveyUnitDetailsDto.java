package fr.insee.survey.datacollectionmanagement.questioning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SurveyUnitDetailsDto {

    @NotBlank
    private String idSu;
    private String identificationCode;
    private String identificationName;
    private SurveyUnitAddressDto address;
    private boolean hasQuestionings;
    private Set<SurveyUnitCommentOutputDto> comments;

}
