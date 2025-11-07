package fr.insee.survey.datacollectionmanagement.user.service.stub;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

public class SurveyUnitServiceStub implements SurveyUnitService {

    @Setter
    private List<SurveyUnit> surveyUnits = new ArrayList<>();


    @Override
    public SurveyUnit findbyId(String idSu) {
        return null;
    }

    @Override
    public Optional<SurveyUnit> findOptionalById(String idSu) {
        return Optional.empty();
    }

    @Override
    public Set<String> findMissingIds(Set<String> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Set.of();
        }

        Set<String> existingIdentifiers = surveyUnits.stream().map(SurveyUnit::getIdSu).collect(Collectors.toSet());

        Set<String> missingIdentifiers = new HashSet<>(identifiers);
        missingIdentifiers.removeAll(existingIdentifiers);

        return missingIdentifiers;
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
    public SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public void deleteSurveyUnit(String id) {
        //not used
    }

    @Override
    public Page<SearchSurveyUnitDto> findByParameter(String searchParam, Pageable pageable) {
        return null;
    }

    @Override
    public List<SearchSurveyUnitContactDto> findContactsBySurveyUnitId(String id) {
        return List.of();
    }

    @Override
    public SurveyUnitDetailsDto getDetailsById(String id) {
        return null;
    }

    @Override
    public List<String> getCampaignIds(String surveyUnitId) {
        return List.of();
    }
}
