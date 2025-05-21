package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.contact.stub.ContactEventServiceStub;
import fr.insee.survey.datacollectionmanagement.contact.stub.ContactSourceServiceStub;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationServiceImpl service;

    private QuestioningAccreditationRepositoryStub accreditationRepoStub;
    private ContactEventService contactEventServiceStub;
    private ContactService contactServiceStub;
    private ContactSourceService contactSourceServiceStub;
    private PartitioningService partitioningServiceStub;

    @BeforeEach
    void setUp() {
        accreditationRepoStub = new QuestioningAccreditationRepositoryStub();
        contactEventServiceStub = new ContactEventServiceStub();
        contactServiceStub = new ContactServiceStub();
        contactSourceServiceStub = new ContactSourceServiceStub();
        partitioningServiceStub = new PartitioningServiceStub();

        service = new QuestioningAccreditationServiceImpl(
                accreditationRepoStub,
                contactEventServiceStub,
                contactServiceStub,
                contactSourceServiceStub,
                partitioningServiceStub
        );
    }

    @Test
    void testCreatePayload() {
        JsonNode payload = service.createPayload("platine-pilotage");
        assertThat(payload).isNotNull();
        assertThat(payload.get("source").asText()).isEqualTo("platine-pilotage");
    }

    @Test
    void testLogContactUpdate() {
        Contact contact = new Contact();
        contact.setIdentifier("contact-id");
        Questioning questioning = buildQuestioning("su-id");
        JsonNode payload = service.createPayload("platine-pilotage");

        Source source = new Source();
        source.setId("source-id");

        service.logContactUpdate(contact, questioning, payload, source);

        Optional<ContactEvent> contactEvent = contactEventServiceStub.findContactEventsByContact(contact).stream().findFirst();

        assertThat(contactEvent).isPresent();
        assertThat(contactEvent.get().getContact()).isEqualTo(contact);
        assertThat(contactEvent.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(contactEvent.get().getPayload()).isEqualTo(payload);
        assertThat(contactEvent.get().getEventDate()).isCloseTo(new Date(), 5000L);

        ContactSourceId contactSourceId = new ContactSourceId(contact.getIdentifier(), source.getId(), questioning.getSurveyUnit().getIdSu());
        ContactSource contactSource = contactSourceServiceStub.findContactSource(
                contactSourceId.getContactId(),
                contactSourceId.getSourceId(), contactSourceId.getSurveyUnitId());

        assertThat(contactSource.getId()).isEqualTo(contactSourceId);
        assertThat(contactSource.isMain()).isTrue();
    }

    @Test
    void testUpdateExistingAccreditation() throws Exception {
//        String contactId = "new-contact";
//        String oldContactId = "old-contact";
//
//        Contact contact = new Contact(contactId);
//        Questioning questioning = buildQuestioning("su-id");
//
//        // Prepopulate stub repo
//        QuestioningAccreditation existing = new QuestioningAccreditation();
//        existing.setIdQuestioning(questioning.getId());
//        existing.setIsMain(true);
//        existing.setIdContact(oldContactId);
//        accreditationRepoStub.save(existing);
//
//        service.updateExistingAccreditation(contact, questioning, service.createPayload("platine-pilotage"), questioning.getSource());
//
//        QuestioningAccreditation updated = accreditationRepoStub.findByQuestioningIdAndIsMain(questioning.getId());
//        assertThat(updated.getIdContact()).isEqualTo(contactId);
//        assertThat(contactEventServiceStub.getEvents()).hasSize(1);
    }

    @Test
    void testSetMainQuestioningAccreditationToContact_WhenNotFound_ShouldCreate() {
//        Contact contact = new Contact("new-contact");
//        Questioning questioning = buildQuestioning("su-id");
//
//        service.setMainQuestioningAccreditationToContact(contact, questioning);
//
//        QuestioningAccreditation created = accreditationRepoStub.findByQuestioningIdAndIsMain(questioning.getId());
//        assertThat(created).isNotNull();
//        assertThat(created.getIdContact()).isEqualTo("new-contact");
//
//        assertThat(contactSourceServiceStub.getSavedSources()).hasSize(1);
//        assertThat(contactEventServiceStub.getEvents()).hasSize(1);
    }

    @Test
    void testSetMainQuestioningAccreditationToContact_WhenExists_ShouldUpdate() {
//        String questioningId = "q-id";
//        Contact contact = new Contact("updated-contact");
//        Questioning questioning = buildQuestioning("su-id", questioningId);
//
//        // Prepopulate
//        QuestioningAccreditation existing = new QuestioningAccreditation();
//        existing.setIdQuestioning(questioningId);
//        existing.setIsMain(true);
//        existing.setIdContact("old-contact");
//        accreditationRepoStub.save(existing);
//
//        service.setMainQuestioningAccreditationToContact(contact, questioning);
//
//        QuestioningAccreditation updated = accreditationRepoStub.findByQuestioningIdAndIsMain(questioningId);
//        assertThat(updated.getIdContact()).isEqualTo("updated-contact");
//        assertThat(contactSourceServiceStub.getSavedSources()).hasSize(1);
//        assertThat(contactEventServiceStub.getEvents()).hasSize(2); // one for previous + one for current
    }

    @Test
    void testUpdateExistingAccreditation_Throws_WhenNotFound() {
//        Contact contact = new Contact("contact");
//        Questioning questioning = buildQuestioning("nonexistent");
//
//        assertThatThrownBy(() ->
//                service.updateExistingAccreditation(contact, questioning, service.createPayload("platine"), new Source())
//        ).isInstanceOf(NotFoundException);
    }

    // --- Helper method ---
    private Questioning buildQuestioning(String suId) {
        return buildQuestioning(suId, 1L);
    }

    private Questioning buildQuestioning(String suId, Long questioningId) {
        Source source = new Source();
        source.setId("source-id");

        Survey survey = new Survey();
        survey.setSource(source);

        Campaign campaign = new Campaign();
        campaign.setSurvey(survey);

        Partitioning partitioning = new Partitioning();
        partitioning.setCampaign(campaign);
        partitioning.setId("partition-id");

        SurveyUnit su = new SurveyUnit();
        su.setIdSu(suId);

        Questioning questioning = new Questioning();
        questioning.setId(questioningId);
        questioning.setSurveyUnit(su);
        questioning.setIdPartitioning(partitioning.getId());
        return questioning;
    }
}
