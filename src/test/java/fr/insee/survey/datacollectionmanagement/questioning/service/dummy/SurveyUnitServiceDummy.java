package fr.insee.survey.datacollectionmanagement.questioning.service.dummy;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SurveyUnitServiceDummy implements SurveyUnitService {
    @Override
    public SurveyUnit findbyId(String idSu) {
        return null;
    }

    @Override
    public Page<SearchSurveyUnitDto> findbyIdentifier(String id, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchSurveyUnitDto> findbyIdentificationCode(String identificationCode, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchSurveyUnitDto> findbyIdentificationName(String identificationName, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public SurveyUnit saveSurveyUnitAddressComments(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public void deleteSurveyUnit(String id) {

    }
}
