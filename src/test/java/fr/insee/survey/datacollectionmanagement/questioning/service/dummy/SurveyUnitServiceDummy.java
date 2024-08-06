package fr.insee.survey.datacollectionmanagement.questioning.service.dummy;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SurveyUnitServiceDummy implements SurveyUnitService {
    @Override
    public SurveyUnit findbyId(String idSu) {
        return null;
    }

    @Override
    public List<SurveyUnit> findbyIdentificationCode(String identificationCode) {
        return null;
    }

    @Override
    public List<SurveyUnit> findbyIdentificationName(String identificationName) {
        return null;
    }

    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchSurveyUnitDto> findByParameter(String param, Pageable pageable) {
        return null;
    }

    @Override
    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public void deleteSurveyUnit(String id) {

    }
}
