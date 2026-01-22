package fr.insee.survey.datacollectionmanagement.contact.service;


import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import org.springframework.stereotype.Service;

@Service
public interface ContactSourceService {
    ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId);

    ContactSource findMainContactSourceBySourceAndSurveyUnit(String sourceId, String surveyUnitId);

    ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId, boolean isMain);

    void deleteContactSource(String contactId, String sourceId, String surveyUnitId);
}