package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;

import java.util.ArrayList;

public class ContactSourceServiceStub implements ContactSourceService {

    ArrayList<ContactSource> contactSources = new ArrayList<>();

    @Override
    public ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId) {
        ContactSourceId contactSourceId = new ContactSourceId(sourceId, contactId, surveyUnitId);
        return contactSources.stream().filter(contactSource -> contactSource
                .getId().equals(contactSourceId)).findFirst()
                .orElseThrow(() -> new NotFoundException(
                String.format("ContactSource not found for %s, %s and %s", contactId, sourceId, surveyUnitId)));
    }

    @Override
    public ContactSource findMainContactSourceBySourceAndSurveyUnit(String sourceId, String surveyUnitId) {
        return null;
    }

    @Override
    public ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId, boolean isMain) {
        ContactSource contactSource = new ContactSource();
        ContactSourceId contactSourceId = new ContactSourceId(sourceId, contactId, surveyUnitId);
        contactSource.setId(contactSourceId);
        contactSources.add(contactSource);
        contactSource.setMain(isMain);
        return contactSource;
    }

    @Override
    public void deleteContactSource(String contactId, String sourceId, String surveyUnitId) {
        contactSources.remove(findContactSource(contactId, sourceId, surveyUnitId));
    }
}
