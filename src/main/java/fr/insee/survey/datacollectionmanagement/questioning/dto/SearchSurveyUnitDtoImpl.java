package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchSurveyUnitDtoImpl implements SearchSurveyUnitDto{

    private String idSu;
    private String identificationCode;
    private String identificationName;

}
