package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    @Override
    public Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity) {
        return surveyRepository.findBySourceIdYearPeriodicity(pageable,sourceId, year, periodicity);
    }

    @Override
    public Survey findById(String id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Survey %s not found", id)));
    }

    @Override
    public Page<Survey> findAll(Pageable pageable) {
        return surveyRepository.findAll(pageable);
    }

    @Override
    public Survey insertOrUpdateSurvey(Survey survey) {
        try {
            Survey surveyBase = findById(survey.getId());
            log.info("Update survey with the id {}", survey.getId());
            survey.setCampaigns(surveyBase.getCampaigns());
        } catch (NotFoundException e) {
            log.info("Create survey with the id {}", survey.getId());
        }
        return surveyRepository.save(survey);

    }

    @Override
    public void deleteSurveyById(String id) {
        surveyRepository.deleteById(id);
    }

    @Override
    public Survey addCampaignToSurvey(Survey survey, Campaign campaign) {

        Set<Campaign> campaigns;
        try {
            Survey surveyBase = findById(survey.getId());
            campaigns = surveyBase.getCampaigns();
            if(!isCampaignPresent(campaign, surveyBase)) {
                campaigns.add(campaign);
            }
        }
        catch (NotFoundException e){
            campaigns = Set.of(campaign);

        }
        survey.setCampaigns(campaigns);
        return survey;

    }


    private boolean isCampaignPresent(Campaign c, Survey s) {
        for (Campaign camp : s.getCampaigns()) {
            if (camp.getId().equals(c.getId())) {
                return true;
            }
        }
        return false;
    }

}
