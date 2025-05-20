package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;

public class ContactSourceServiceStub implements ContactSourceService {
    @Override
    public ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId) {
        return null;
    }

    @Override
    public ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId) {
        return null;
    }

    @Override
    public void deleteContactSource(String contactId, String sourceId, String surveyUnitId) {
        //not used
    }
}
