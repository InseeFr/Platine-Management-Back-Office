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
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationServiceImpl service;

    private QuestioningAccreditationRepositoryStub accreditationRepoStub;
    private ContactEventService contactEventServiceStub;
    private ContactService contactServiceStub;
    private ContactSourceService contactSourceServiceStub;
    private PartitioningService partitioningServiceStub;
    private ViewService viewServiceStub;

    @BeforeEach
    void setUp() {
        accreditationRepoStub = new QuestioningAccreditationRepositoryStub();
        contactEventServiceStub = new ContactEventServiceStub();
        contactServiceStub = new ContactServiceStub();
        contactSourceServiceStub = new ContactSourceServiceStub();
        partitioningServiceStub = new PartitioningServiceStub();
        viewServiceStub = new ViewServiceStub();


        service = new QuestioningAccreditationServiceImpl(
                accreditationRepoStub,
                contactEventServiceStub,
                contactServiceStub,
                contactSourceServiceStub,
                partitioningServiceStub,
                viewServiceStub
        );
    }

    @Test
    @DisplayName("Generate payload")
    void testCreatePayload() {
        JsonNode payload = service.createPayload("platine-pilotage");
        assertThat(payload).isNotNull();
        assertThat(payload.get("source").asText()).isEqualTo("platine-pilotage");
    }

    @Test
    @DisplayName("Add an update ContactEvent and a ContactSource")
    void testLogContactAccreditationGainUpdate() {
        Contact contact = new Contact();
        contact.setIdentifier("contact-id");
        Questioning questioning = buildQuestioning("su-id");
        JsonNode payload = service.createPayload("platine-pilotage");

        Campaign campaign = partitioningServiceStub.findById("partition-id").getCampaign();

        service.logContactAccreditationGainUpdate(contact, questioning, payload, campaign );

        Optional<ContactEvent> contactEvent = contactEventServiceStub.findContactEventsByContact(contact).stream().findFirst();

        assertThat(contactEvent).isPresent();
        assertThat(contactEvent.get().getContact()).isEqualTo(contact);
        assertThat(contactEvent.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(contactEvent.get().getPayload()).isEqualTo(payload);
        assertThat(contactEvent.get().getEventDate()).isCloseTo(new Date(), 5000L);

        ContactSourceId contactSourceId = new ContactSourceId(
                campaign.getSurvey().getSource().getId(),
                contact.getIdentifier(),
                questioning.getSurveyUnit().getIdSu());

        ContactSource contactSource = contactSourceServiceStub.findContactSource(
                contactSourceId.getContactId(),
                contactSourceId.getSourceId(), contactSourceId.getSurveyUnitId());

        assertThat(contactSource.getId()).isEqualTo(contactSourceId);
        assertThat(contactSource.isMain()).isTrue();
    }

    @Test
    @DisplayName("Add an update ContactEvent and remove ContactSource")
    void testLogContactAccreditationLossUpdate() {
        Contact contact = new Contact();
        contact.setIdentifier("contact-id");
        Questioning questioning = buildQuestioning("su-id");
        JsonNode payload = service.createPayload("platine-pilotage");
        Campaign campaign = partitioningServiceStub.findById("partition-id").getCampaign();

        View view = new View();
        view.setCampaignId(campaign.getId());
        view.setIdSu(questioning.getSurveyUnit().getIdSu());
        view.setIdentifier(contact.getIdentifier());
        view.setId(1L);
        viewServiceStub.saveView(view);

        contactSourceServiceStub.saveContactSource(
                contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                questioning.getSurveyUnit().getIdSu(), true);

        service.logContactAccreditationLossUpdate(contact,
                questioning,
                payload,
                campaign);

        Optional<ContactEvent> contactEvent = contactEventServiceStub.findContactEventsByContact(contact).stream().findFirst();

        assertThat(contactEvent).isPresent();
        assertThat(contactEvent.get().getContact()).isEqualTo(contact);
        assertThat(contactEvent.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(contactEvent.get().getPayload()).isEqualTo(payload);
        assertThat(contactEvent.get().getEventDate()).isCloseTo(new Date(), 5000L);
        assertThatThrownBy(() -> contactSourceServiceStub.findContactSource(
                contact.getIdentifier(), campaign.getSurvey().getSource().getId(), questioning.getSurveyUnit().getIdSu()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("ContactSource not found for %s, %s and %s",
                        contact.getIdentifier(),
                        campaign.getSurvey().getSource().getId(),
                        questioning.getSurveyUnit().getIdSu()));
    }

    @Test
    @DisplayName("Update existing main accreditation to new contact")
    void testUpdateExistingMainAccreditationToNewContact() {
        String contactId = "new-contact";
        String oldContactId = "old-contact";

        Contact oldContact = new Contact();
        oldContact.setIdentifier(oldContactId);
        Contact newContact = new Contact();
        newContact.setIdentifier(contactId);
        Questioning questioning = buildQuestioning("su-id");

        contactServiceStub.saveContact(oldContact);
        contactServiceStub.saveContact(newContact);

        Campaign campaign = partitioningServiceStub.findById("partition-id").getCampaign();
        contactSourceServiceStub.saveContactSource(oldContactId, campaign.getSurvey().getSource().getId(),questioning.getSurveyUnit().getIdSu(), true );

        QuestioningAccreditation existingAccreditation = new QuestioningAccreditation();
        existingAccreditation.setQuestioning(questioning);
        existingAccreditation.setMain(true);
        existingAccreditation.setIdContact(oldContactId);
        existingAccreditation.setId(1L);
        accreditationRepoStub.save(existingAccreditation);

        View view = new View();
        view.setCampaignId(campaign.getId());
        view.setIdSu(questioning.getSurveyUnit().getIdSu());
        view.setIdentifier(oldContact.getIdentifier());
        view.setId(1L);
        viewServiceStub.saveView(view);

        service.updateExistingMainAccreditationToNewContact(
                newContact,
                questioning,
                service.createPayload("platine-pilotage"),
                campaign);

        Optional<QuestioningAccreditation> updatedAccreditation = accreditationRepoStub
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(updatedAccreditation).isPresent();
        assertThat(updatedAccreditation.get().getIdContact()).isEqualTo(contactId);
        assertThat(contactEventServiceStub.findContactEventsByContact(oldContact)).hasSize(1);
        assertThatThrownBy(() -> contactSourceServiceStub.findContactSource(
                oldContactId, campaign.getSurvey().getSource().getId(), questioning.getSurveyUnit().getIdSu()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("ContactSource not found for %s, %s and %s",
                        oldContactId,
                        campaign.getSurvey().getSource().getId(),
                        questioning.getSurveyUnit().getIdSu()));
    }

    @Test
    @DisplayName("Should create an accreditation if none existed")
    void testSetMainQuestioningAccreditationToContact_WhenNotFound_ShouldCreate() {
        Contact contact = new Contact();
        contact.setIdentifier("new-contact");
        Questioning questioning = buildQuestioning("su-id");
        service.setMainQuestioningAccreditationToContact(contact, questioning);

        Optional<QuestioningAccreditation> createdAccreditation = accreditationRepoStub.findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(createdAccreditation).isPresent();
        assertThat(createdAccreditation.get().getIdContact()).isEqualTo("new-contact");
        assertThat(createdAccreditation.get().isMain()).isTrue();
        assertThat(createdAccreditation.get().getQuestioning()).isEqualTo(questioning);
        assertThat(createdAccreditation.get().getCreationDate()).isCloseTo(new Date(), 5000);

        Source source = partitioningServiceStub.findById("partition-id").getCampaign().getSurvey().getSource();
        assertThat(contactSourceServiceStub.findContactSource(contact.getIdentifier(), source.getId(), questioning.getSurveyUnit().getIdSu())).isNotNull();
        assertThat(contactEventServiceStub.findContactEventsByContact(contact)).hasSize(1);
    }

    @Test
    @DisplayName("Should update an accreditation if it exists")
    void testSetMainQuestioningAccreditationToContact_WhenExists_ShouldUpdate() {

        String contactId = "new-contact";
        String oldContactId = "old-contact";

        Contact oldContact = new Contact();
        oldContact.setIdentifier(oldContactId);
        Contact newContact = new Contact();
        newContact.setIdentifier(contactId);
        Questioning questioning = buildQuestioning("su-id");

        contactServiceStub.saveContact(oldContact);
        contactServiceStub.saveContact(newContact);

        QuestioningAccreditation existing = new QuestioningAccreditation();
        existing.setId(1L);
        existing.setQuestioning(questioning);
        existing.setMain(true);
        existing.setIdContact(oldContact.getIdentifier());
        existing.setCreationDate(new Date());
        accreditationRepoStub.save(existing);

        Campaign campaign = partitioningServiceStub.findById("partition-id").getCampaign();
        View view = new View();
        view.setCampaignId(campaign.getId());
        view.setIdSu(questioning.getSurveyUnit().getIdSu());
        view.setIdentifier(oldContact.getIdentifier());
        view.setId(1L);
        viewServiceStub.saveView(view);

        service.setMainQuestioningAccreditationToContact(newContact, questioning);

        Optional<QuestioningAccreditation> updatedAccreditation = accreditationRepoStub.findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(updatedAccreditation).isPresent();
        assertThat(updatedAccreditation.get().getIdContact()).isEqualTo(newContact.getIdentifier());
        assertThat(updatedAccreditation.get().isMain()).isEqualTo(existing.isMain());
        assertThat(updatedAccreditation.get().getQuestioning()).isEqualTo(existing.getQuestioning());
        assertThat(updatedAccreditation.get().getCreationDate()).isCloseTo(existing.getCreationDate(), 5000);

        Source source = partitioningServiceStub.findById("partition-id").getCampaign().getSurvey().getSource();
        assertThat(contactSourceServiceStub.findContactSource(newContact.getIdentifier(), source.getId(),
                questioning.getSurveyUnit().getIdSu()).getId().getContactId()).isEqualTo(newContact.getIdentifier());
        assertThat(contactEventServiceStub.findContactEventsByContact(oldContact)).hasSize(1);
        assertThat(contactEventServiceStub.findContactEventsByContact(newContact)).hasSize(1);
    }

    @Test
    @DisplayName("Should throw not found exception when no questioning accreditation found")
    void testUpdateExistingMainAccreditation_ToNewContact_Throws_WhenNotFound() {
        Contact contact = new Contact();
        contact.setIdentifier("new-contact");
        Questioning questioning = buildQuestioning("nonexistent");

        Campaign campaign = partitioningServiceStub.findById("partition-id").getCampaign();

        assertThatThrownBy(() ->
                service.updateExistingMainAccreditationToNewContact(
                        contact,
                        questioning,
                        service.createPayload("platine"),
                        campaign

                        )
        ).isInstanceOf(NotFoundException.class).hasMessage(String.format("QuestioningAccreditation for %s questioningId not found", questioning.getId()));
    }

    private Questioning buildQuestioning(String suId) {
        Source source = new Source();
        source.setId("source-id");

        Survey survey = new Survey();
        survey.setSource(source);

        Campaign campaign = new Campaign();
        campaign.setSurvey(survey);
        campaign.setId("campaign-id");

        Partitioning partitioning = new Partitioning();
        partitioning.setCampaign(campaign);
        partitioning.setId("partition-id");
        partitioningServiceStub.insertOrUpdatePartitioning(partitioning);

        SurveyUnit su = new SurveyUnit();
        su.setIdSu(suId);

        Questioning questioning = new Questioning();
        questioning.setId(1L);
        questioning.setSurveyUnit(su);
        questioning.setIdPartitioning(partitioning.getId());
        return questioning;
    }
}
