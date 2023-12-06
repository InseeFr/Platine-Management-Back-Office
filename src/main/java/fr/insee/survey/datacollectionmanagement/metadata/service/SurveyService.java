package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyService {

    List<Survey> findByYear(int year);

    Survey findById(String id);

    Page<Survey> findAll(Pageable pageable);

    Survey insertOrUpdateSurvey(Survey survey);

    void deleteSurveyById(String id);

    Survey addCampaignToSurvey(Survey survey, Campaign campaign);

}
