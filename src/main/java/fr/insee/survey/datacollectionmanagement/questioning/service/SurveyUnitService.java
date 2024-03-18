package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyUnitService {

    public SurveyUnit findbyId(String idSu);

    public List<SurveyUnit> findbyIdentificationCode(String identificationCode);

    public List<SurveyUnit> findbyIdentificationName(String identificationName);

    public Page<SurveyUnit> findAll(Pageable pageable);

    public Page<SurveyUnit> findByParameters(String idSu, String identificationCode, String identificationName, Pageable pageable);

    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit);
    
    public SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit);

    public void deleteSurveyUnit(String id);

}
