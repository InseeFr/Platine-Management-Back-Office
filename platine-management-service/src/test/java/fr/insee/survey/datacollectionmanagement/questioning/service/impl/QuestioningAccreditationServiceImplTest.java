package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.*;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.*;
import fr.insee.survey.datacollectionmanagement.contact.stub.*;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationServiceImpl service;
    private QuestioningAccreditationRepositoryStub accreditationRepo;
    private ContactEventService contactEventService;
    private ContactService contactService;
    private ContactSourceService contactSourceService;
    private PartitioningService partitioningService;
    private ViewService viewService;

    @BeforeEach
    void initServiceWithStubs() {
        accreditationRepo = new QuestioningAccreditationRepositoryStub();
        contactEventService = new ContactEventServiceStub();
        contactService = new ContactServiceStub();
        contactSourceService = new ContactSourceServiceStub();
        partitioningService = new PartitioningServiceStub();
        viewService = new ViewServiceStub();

        service = new QuestioningAccreditationServiceImpl(
                accreditationRepo, contactEventService, contactService,
                contactSourceService, partitioningService, viewService
        );
    }

    @Test
    @DisplayName("Should generate non-null payload with correct source")
    void shouldGenerateCorrectPayload() {
        JsonNode payload = service.createPayload("platine-pilotage");
        assertThat(payload).isNotNull();
        assertThat(payload.get("source").asText()).isEqualTo("platine-pilotage");
    }

    @Test
    @DisplayName("Should add contact event and contact source on accreditation gain")
    void shouldLogAccreditationGain() {
        Contact contact = createAndSaveContact("contact-id");
        Questioning questioning = createAndRegisterQuestioning("su-id");
        Campaign campaign = getCampaignFromPartition("partition-id");
        JsonNode payload = service.createPayload("platine-pilotage");

        service.logContactAccreditationGainUpdate(contact, questioning, payload, campaign);

        assertContactEvent(contact, ContactEventTypeEnum.update, payload);
        assertContactSourceExists(contact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should add contact event and remove contact source on accreditation loss")
    void shouldLogAccreditationLoss() {
        Contact contact = createAndSaveContact("contact-id");
        Questioning questioning = createAndRegisterQuestioning("su-id");
        Campaign campaign = getCampaignFromPartition("partition-id");
        JsonNode payload = service.createPayload("platine-pilotage");

        setupViewAndSource(contact, campaign, questioning);
        service.logContactAccreditationLossUpdate(contact, questioning, payload, campaign);

        assertContactEvent(contact, ContactEventTypeEnum.update, payload);
        assertContactSourceNotFound(contact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should update existing main accreditation to new contact")
    void shouldUpdateMainAccreditationToNewContact() {
        Contact oldContact = createAndSaveContact("old-contact");
        Contact newContact = createAndSaveContact("new-contact");
        Questioning questioning = createAndRegisterQuestioning("su-id");
        Campaign campaign = getCampaignFromPartition("partition-id");

        saveMainAccreditation(oldContact, questioning);
        setupViewAndSource(oldContact, campaign, questioning);

        JsonNode payload = service.createPayload("platine-pilotage");

        service.updateExistingMainAccreditationToNewContact(newContact, questioning, payload, campaign);

        assertMainAccreditation(newContact, questioning);
        assertContactEvent(oldContact, ContactEventTypeEnum.update, payload);
        assertContactSourceNotFound(oldContact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    // Helper Methods

    private Contact createAndSaveContact(String id) {
        Contact contact = new Contact();
        contact.setIdentifier(id);
        contactService.saveContact(contact);
        return contact;
    }

    private Campaign getCampaignFromPartition(String partitionId) {
        return partitioningService.findById(partitionId).getCampaign();
    }

    private void saveMainAccreditation(Contact contact, Questioning questioning) {
        QuestioningAccreditation acc = new QuestioningAccreditation();
        acc.setId(1L);
        acc.setQuestioning(questioning);
        acc.setMain(true);
        acc.setIdContact(contact.getIdentifier());
        acc.setCreationDate(new Date());
        accreditationRepo.save(acc);
    }

    private void setupViewAndSource(Contact contact, Campaign campaign, Questioning questioning) {
        View view = new View();
        view.setCampaignId(campaign.getId());
        view.setIdSu(questioning.getSurveyUnit().getIdSu());
        view.setIdentifier(contact.getIdentifier());
        view.setId(1L);
        viewService.saveView(view);

        contactSourceService.saveContactSource(contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                questioning.getSurveyUnit().getIdSu(), true);
    }

    private void assertContactEvent(Contact contact, ContactEventTypeEnum type, JsonNode payload) {
        Optional<ContactEvent> event = contactEventService.findContactEventsByContact(contact).stream().findFirst();
        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(type);
        assertThat(event.get().getPayload()).isEqualTo(payload);
        assertThat(event.get().getEventDate()).isCloseTo(new Date(), 5000L);
    }

    private void assertContactSourceExists(String contactId, Campaign campaign, String idSu) {
        ContactSource source = contactSourceService.findContactSource(
                contactId, campaign.getSurvey().getSource().getId(), idSu);
        assertThat(source).isNotNull();
        assertThat(source.getId().getContactId()).isEqualTo(contactId);
    }

    private void assertContactSourceNotFound(String contactId, Campaign campaign, String idSu) {
        assertThatThrownBy(() -> contactSourceService.findContactSource(
                contactId, campaign.getSurvey().getSource().getId(), idSu))
                .isInstanceOf(NotFoundException.class);
    }

    private void assertMainAccreditation(Contact contact, Questioning questioning) {
        Optional<QuestioningAccreditation> acc = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(acc).isPresent();
        assertThat(acc.get().getIdContact()).isEqualTo(contact.getIdentifier());
        assertThat(acc.get().isMain()).isTrue();
    }

    @Test
    @DisplayName("Should create and save new questioning accreditation")
    void shouldCreateQuestioningAccreditation() {
        Questioning questioning = createAndRegisterQuestioning("su-id");
        Date now = new Date();

        service.createQuestioningAccreditation(questioning, true, "contact-id", now);

        Optional<QuestioningAccreditation> saved = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(saved).isPresent();
        assertThat(saved.get().getIdContact()).isEqualTo("contact-id");
        assertThat(saved.get().getQuestioning()).isEqualTo(questioning);
        assertThat(saved.get().isMain()).isTrue();
        assertThat(saved.get().getCreationDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create new main accreditation if none exists")
    void shouldSetMainAccreditationWhenNoneExists() {
        Contact contact = createAndSaveContact("new-contact");
        Questioning questioning = createAndRegisterQuestioning("su-id");

        service.setMainQuestioningAccreditationToContact(contact, questioning);

        Optional<QuestioningAccreditation> saved = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(saved).isPresent();
        assertThat(saved.get().getIdContact()).isEqualTo("new-contact");
    }

    @Test
    @DisplayName("Should update existing main accreditation to new contact")
    void shouldUpdateExistingMainAccreditation() {
        Contact oldContact = createAndSaveContact("old");
        Contact newContact = createAndSaveContact("new");
        Questioning questioning = createAndRegisterQuestioning("su-id");

        saveMainAccreditation(oldContact, questioning);

        service.setMainQuestioningAccreditationToContact(newContact, questioning);

        Optional<QuestioningAccreditation> updated = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(updated).isPresent();
        assertThat(updated.get().getIdContact()).isEqualTo("new");
    }

    private Questioning createAndRegisterQuestioning(String suId) {
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
        partitioningService.insertOrUpdatePartitioning(partitioning);

        SurveyUnit su = new SurveyUnit();
        su.setIdSu(suId);

        Questioning questioning = new Questioning();
        questioning.setId(1L);
        questioning.setSurveyUnit(su);
        questioning.setIdPartitioning(partitioning.getId());

        return questioning;
    }
}
