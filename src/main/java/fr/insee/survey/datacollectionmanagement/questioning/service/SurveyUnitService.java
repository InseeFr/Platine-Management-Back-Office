package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyUnitService {

    SurveyUnit findbyId(String idSu);

    List<SurveyUnit> findbyIdentificationCode(String identificationCode);

    List<SurveyUnit> findbyIdentificationName(String identificationName);

    Page<SurveyUnit> findAll(Pageable pageable);

    Page<SearchSurveyUnitDto> findByParameter(String param, Pageable pageable);

    SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit);
    
    SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit);

    void deleteSurveyUnit(String id);

}
