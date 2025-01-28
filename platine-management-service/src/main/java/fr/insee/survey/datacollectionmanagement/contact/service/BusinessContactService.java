package fr.insee.survey.datacollectionmanagement.contact.service;

import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactsDto;
import org.springframework.stereotype.Service;

@Service
public interface BusinessContactService {

    BusinessContactsDto findMainContactByCampaignAndSurveyUnit(String campaignId, String surveyUnitId);

}
