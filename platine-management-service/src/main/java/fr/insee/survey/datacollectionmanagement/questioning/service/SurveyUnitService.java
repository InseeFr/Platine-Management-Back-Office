package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SurveyUnitService {

    SurveyUnit findbyId(String idSu);

    Optional<SurveyUnit> findOptionalById(String idSu);

    Page<SearchSurveyUnitDto> findbyIdentifier(String id, Pageable pageable);

    Page<SearchSurveyUnitDto> findbyIdentificationCode(String identificationCode, Pageable pageable);

    Page<SearchSurveyUnitDto> findbyIdentificationName(String identificationName, Pageable pageable);

    Page<SurveyUnit> findAll(Pageable pageable);

    SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit);

    SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit);

    void deleteSurveyUnit(String id);

    Page<SearchSurveyUnitDto> findByParameter(String searchParam, Pageable pageable);

    List<SearchSurveyUnitContactDto> findContactsBySurveyUnitId(String id);

    SurveyUnitDetailsDto getDetailsById(String id);

    List<String> getCampaignIds(String surveyUnitId);
}
