package fr.insee.survey.datacollectionmanagement.contact.service;


import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactSourceService {
    public ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId);

    public ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId);

    public void deleteContactSource(String contactId, String sourceId, String surveyUnitId);
}