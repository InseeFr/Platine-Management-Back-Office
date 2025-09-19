package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactSourceRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactSourceServiceImpl implements ContactSourceService {
    private final ContactSourceRepository contactSourceRepository;

    @Override
    public ContactSource findContactSource(String contactId, String sourceId, String surveyUnitId) {
        ContactSourceId contactSourceId = new ContactSourceId(sourceId, contactId, surveyUnitId);
        return contactSourceRepository.findById(contactSourceId)
                .orElseThrow(() -> new NotFoundException(String.format("ContactSource not found for %s, %s and %s", contactId, sourceId, surveyUnitId)));
    }

    @Override
    public ContactSource findMainContactSourceBySourceAndSurveyUnit(String sourceId, String surveyUnitId) {
        return contactSourceRepository.findByIdSourceIdAndIdSurveyUnitIdAndIsMain(sourceId, surveyUnitId, true);
    }

    @Override
    public ContactSource saveContactSource(String contactId, String sourceId, String surveyUnitId, boolean isMain) {
        ContactSource contactSource = new ContactSource();
        contactSource.setId(new ContactSourceId(sourceId, contactId, surveyUnitId));
        contactSource.setMain(isMain);
        return contactSourceRepository.save(contactSource);
    }

    @Override
    public void deleteContactSource(String contactId, String sourceId, String surveyUnitId) {
        ContactSourceId contactSourceId = new ContactSourceId(sourceId, contactId, surveyUnitId);
        contactSourceRepository.deleteById(contactSourceId);
    }
}