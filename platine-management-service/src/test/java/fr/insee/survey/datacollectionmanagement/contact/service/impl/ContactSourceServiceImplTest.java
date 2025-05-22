package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactSourceRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.contact.stub.ContactSourceRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactSourceServiceImplTest {

    public ContactSourceService contactSourceService;
    public ContactSourceRepository contactSourceRepository;

    @BeforeEach
    void init() {
        contactSourceRepository = new ContactSourceRepositoryStub();
        contactSourceService = new ContactSourceServiceImpl(contactSourceRepository);
    }

    @Test
    @DisplayName("Should save, find and delete a contact source")
    void SaveFindAndDeleteContactSource() {
        String contactId = "contact-id";
        String sourceId = "source-id";
        String surveyUnitId = "su-id";
        boolean isMain = true;

        ContactSourceId contactSourceId = new ContactSourceId(sourceId, contactId, surveyUnitId);

         ContactSource savedContactSource = contactSourceService.saveContactSource(
                contactSourceId.getContactId(),
                contactSourceId.getSourceId(),
                contactSourceId.getSurveyUnitId(), isMain);

        assertThat(contactSourceRepository.findById(contactSourceId)).isEqualTo(savedContactSource);

        ContactSource foundContactSource = contactSourceService.findContactSource(
                contactSourceId.getContactId(),
                contactSourceId.getContactId(),
                contactSourceId.getSurveyUnitId());

        assertThat(foundContactSource).isEqualTo(savedContactSource);

        contactSourceService.deleteContactSource(
                contactSourceId.getContactId(),
                contactSourceId.getSourceId(),
                contactSourceId.getSurveyUnitId());

        assertThat(contactSourceRepository.findById(contactSourceId)).isNull();

    }
}
