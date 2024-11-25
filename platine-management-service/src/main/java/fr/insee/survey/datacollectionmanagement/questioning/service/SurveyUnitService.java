package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyUnitService {

    SurveyUnit findbyId(String idSu);

    Page<SearchSurveyUnitDto> findbyIdentifier(String id, Pageable pageable);

    Page<SearchSurveyUnitDto> findbyIdentificationCode(String identificationCode, Pageable pageable);

    Page<SearchSurveyUnitDto>  findbyIdentificationName(String identificationName, Pageable pageable);

    Page<SurveyUnit> findAll(Pageable pageable);

    SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit);
    
    SurveyUnit saveSurveyUnitAddressComments(SurveyUnit surveyUnit);

    void deleteSurveyUnit(String id);

}
