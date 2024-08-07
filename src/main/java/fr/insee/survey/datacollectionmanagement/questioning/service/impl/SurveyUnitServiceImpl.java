package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitServiceImpl implements SurveyUnitService {

    private final SurveyUnitRepository surveyUnitRepository;

    private final SurveyUnitAddressRepository surveyUnitAddressRepository;

    @Override
    public SurveyUnit findbyId(String idSu) {
        return surveyUnitRepository.findById(idSu).orElseThrow(() -> new NotFoundException(String.format("SurveyUnit %s not found", idSu)));
    }

    @Override
    public List<SurveyUnit> findbyIdentificationCode(String identificationCode) {
        return surveyUnitRepository.findAllByIdentificationCode(identificationCode);
    }

    @Override
    public List<SurveyUnit> findbyIdentificationName(String identificationName) {
        return surveyUnitRepository.findByIdentificationNameIgnoreCase(identificationName);
    }

    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return surveyUnitRepository.findAll(pageable);
    }

    @Override
    public Page<SearchSurveyUnitDto> findByParameter(String param, Pageable pageable) {
        return surveyUnitRepository.findByParameters(StringUtils.upperCase(param), pageable);
    }

    @Override
    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit) {
        return surveyUnitRepository.save(surveyUnit);
    }

    @Override
    public SurveyUnit saveSurveyUnitAddressComments(SurveyUnit surveyUnit) {

        try {
            SurveyUnit existingSurveyUnit = findbyId(surveyUnit.getIdSu());
            surveyUnit.setSurveyUnitComments(existingSurveyUnit.getSurveyUnitComments());
        } catch (NotFoundException e) {
            log.debug("Survey unit does not exist");
        }
        if (surveyUnit.getSurveyUnitAddress() != null) {
            try {
                SurveyUnit existingSurveyUnit = findbyId(surveyUnit.getIdSu());
                if (existingSurveyUnit.getSurveyUnitAddress() != null) {
                    surveyUnit.getSurveyUnitAddress().setId(existingSurveyUnit.getSurveyUnitAddress().getId());
                }
            } catch (NotFoundException e) {
                log.debug("Survey unit does not exist");
            }
            surveyUnitAddressRepository.save(surveyUnit.getSurveyUnitAddress());

        }
        return surveyUnitRepository.save(surveyUnit);

    }

    @Override
    public void deleteSurveyUnit(String id) {
        surveyUnitRepository.deleteById(id);

    }

}
