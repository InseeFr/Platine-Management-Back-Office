package fr.insee.survey.datacollectionmanagement.contact.service;


import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import org.springframework.stereotype.Service;

@Service
public interface ContactSourceService {
    public ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId);

    public ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId, boolean isMain);

    public void deleteContactSource(String contactId, String sourceId, String surveyUnitId);
}