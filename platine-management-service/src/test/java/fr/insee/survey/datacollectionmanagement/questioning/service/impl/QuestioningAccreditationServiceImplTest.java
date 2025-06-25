package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.*;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.*;
import fr.insee.survey.datacollectionmanagement.contact.stub.*;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.*;
import fr.insee.survey.datacollectionmanagement.util.ServiceJsonUtil;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationServiceImpl service;
    private QuestioningAccreditationRepository accreditationRepo;
    private ContactEventService contactEventService;
    private ContactRepository contactRepository;
    private ContactSourceService contactSourceService;
    private PartitioningService partitioningService;
    private ViewService viewService;
    private QuestioningRepository questioningRepository;

    @BeforeEach
    void initServiceWithStubs() {
        accreditationRepo = new QuestioningAccreditationRepositoryStub();
        contactEventService = new ContactEventServiceStub(new ModelMapper());
        contactRepository = new ContactRepositoryStub();
        contactSourceService = new ContactSourceServiceStub();
        partitioningService = new PartitioningServiceStub();
        viewService = new ViewServiceStub();
        questioningRepository = new QuestioningRepositoryStub();

        service = new QuestioningAccreditationServiceImpl(
                accreditationRepo, contactEventService,
                contactSourceService, partitioningService, viewService,
                questioningRepository, contactRepository
        );
    }

    @Test
    @DisplayName("Should add contact event and contact source on accreditation gain")
    void shouldLogAccreditationGain() {
        Contact contact = createAndSaveContact("contact-id");
        Questioning questioning = createAndRegisterQuestioning();
        Campaign campaign = getCampaignFromPartition();
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");

        service.logContactAccreditationGainUpdate(contact, questioning.getSurveyUnit().getIdSu(), payload, campaign);

        assertContactEvent(contact, payload);
        assertContactSourceExists(contact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should add contact event and remove contact source on accreditation loss")
    void shouldLogAccreditationLoss() {
        Contact contact = createAndSaveContact("contact-id");
        Questioning questioning = createAndRegisterQuestioning();
        Campaign campaign = getCampaignFromPartition();
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");

        setupViewAndSource(contact, campaign, questioning);
        assertThat(viewService.findViewByIdentifier(contact.getIdentifier())).hasSize(1);

        service.logContactAccreditationLossUpdate(contact, questioning.getSurveyUnit().getIdSu(), payload, campaign);

        assertThat(viewService.findViewByIdentifier(contact.getIdentifier())).hasSize(1);
        assertContactEvent(contact, payload);
        assertContactSourceNotFound(contact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should add contact event and remove contact source on accreditation loss, handle a missing view")
    void shouldLogAccreditationLossMissingView() {
        Contact contact = createAndSaveContact("contact-id");
        Questioning questioning = createAndRegisterQuestioning();
        Campaign campaign = getCampaignFromPartition();
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");
        setupSource(contact, campaign, questioning);


        assertThat(viewService.findViewByIdentifier(contact.getIdentifier())).isEmpty();

        service.logContactAccreditationLossUpdate(contact, questioning.getSurveyUnit().getIdSu(), payload, campaign);
        assertThat(viewService.findViewByIdentifier(contact.getIdentifier()).getFirst().getIdSu()).isNull();
        assertThat(viewService.findViewByIdentifier(contact.getIdentifier()).getFirst().getCampaignId()).isNull();
        assertThat(viewService.findViewByIdentifier(contact.getIdentifier()).getFirst().getIdentifier()).isEqualTo(contact.getIdentifier());

        assertContactEvent(contact, payload);
        assertContactSourceNotFound(contact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should update existing main accreditation to new contact")
    void shouldUpdateMainAccreditationToNewContact() {
        Contact oldContact = createAndSaveContact("old-contact");
        Contact newContact = createAndSaveContact("new-contact");
        Questioning questioning = createAndRegisterQuestioning();
        Campaign campaign = getCampaignFromPartition();
        QuestioningAccreditation qa = saveMainAccreditation(oldContact, questioning);

        setupViewAndSource(oldContact, campaign, questioning);

        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");

        service.updateExistingMainAccreditationToNewContact(qa, newContact, questioning.getSurveyUnit().getIdSu(), payload, campaign);

        assertMainAccreditation(newContact, questioning);
        assertContactEvent(oldContact, payload);
        assertContactSourceNotFound(oldContact.getIdentifier(), campaign, questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Should throw not found exception when questioning does not exist")
    void shouldNotFindQuestioning()
    {
        UUID questioningId = UUID.randomUUID();
        assertThatThrownBy(() -> service.setMainQuestioningAccreditationToContact(
                "contact-id", questioningId))
                .isInstanceOf(NotFoundException.class).hasMessage(String.format("Missing Questioning with id %s", questioningId));
    }

    private Contact createAndSaveContact(String id) {
        Contact contact = new Contact();
        contact.setIdentifier(id);
        contactRepository.save(contact);
        return contact;
    }

    private Campaign getCampaignFromPartition() {
        return partitioningService.findById("partition-id").getCampaign();
    }

    private QuestioningAccreditation saveMainAccreditation(Contact contact, Questioning questioning) {
        QuestioningAccreditation acc = new QuestioningAccreditation();
        acc.setId(1L);
        acc.setQuestioning(questioning);
        acc.setMain(true);
        acc.setIdContact(contact.getIdentifier());
        acc.setCreationDate(new Date());
        accreditationRepo.save(acc);
        return acc;
    }

    private void setupSource(Contact contact, Campaign campaign, Questioning questioning) {
        contactSourceService.saveContactSource(contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                questioning.getSurveyUnit().getIdSu(), true);
    }

    private void setupViewAndSource(Contact contact, Campaign campaign, Questioning questioning) {
        View view = new View();
        view.setCampaignId(campaign.getId());
        view.setIdSu(questioning.getSurveyUnit().getIdSu());
        view.setIdentifier(contact.getIdentifier());
        view.setId(1L);
        viewService.saveView(view);
        setupSource(contact, campaign, questioning);
    }

    private void assertContactEvent(Contact contact, JsonNode payload) {
        Optional<ContactEvent> event = contactEventService.findContactEventsByContact(contact).stream().findFirst();
        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(event.get().getPayload()).isEqualTo(payload);
        assertThat(event.get().getEventDate()).isCloseTo(new Date(), 5000L);
    }

    private void assertContactSourceExists(String contactId, Campaign campaign, String idSu) {
        ContactSource source = contactSourceService.findContactSource(
                contactId, campaign.getSurvey().getSource().getId(), idSu);
        assertThat(source).isNotNull();
        assertThat(source.getId().getContactId()).isEqualTo(contactId);
    }

    private void assertContactSourceNotFound(String contactId, Campaign campaign, String idSu)
    {
        String campaignId = campaign.getSurvey().getSource().getId();
        assertThatThrownBy(() -> contactSourceService.findContactSource(
                contactId, campaignId, idSu))
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
        Questioning questioning = createAndRegisterQuestioning();
        Date now = new Date();
        Contact contact = createAndSaveContact("contact-id");
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");
        Campaign campaign = getCampaignFromPartition();

        service.createQuestioningAccreditation(questioning, true, contact, payload , now, campaign);

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
        Questioning questioning = createAndRegisterQuestioning();

        service.setMainQuestioningAccreditationToContact(contact.getIdentifier(), questioning.getId());

        Optional<QuestioningAccreditation> saved = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(saved).isPresent();
        assertThat(saved.get().getIdContact()).isEqualTo("new-contact");
    }

    @Test
    @DisplayName("Should update existing main accreditation to new contact")
    void shouldUpdateExistingMainAccreditation() {
        Questioning questioning = createAndRegisterQuestioning();
        Contact oldContact = createAndSaveContact("old");
        Contact newContact = createAndSaveContact("new");
        Campaign campaign = getCampaignFromPartition();

        saveMainAccreditation(oldContact, questioning);
        setupViewAndSource(oldContact, campaign, questioning);
        saveMainAccreditation(oldContact, questioning);

        service.setMainQuestioningAccreditationToContact(newContact.getIdentifier(), questioning.getId());

        Optional<QuestioningAccreditation> updated = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        assertThat(updated).isPresent();
        assertThat(updated.get().getIdContact()).isEqualTo("new");
    }

    private Questioning createAndRegisterQuestioning() {
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
        su.setIdSu("su-id");

        Questioning questioning = new Questioning();
        questioning.setId(UUID.randomUUID());
        questioning.setSurveyUnit(su);
        questioning.setIdPartitioning(partitioning.getId());
        questioningRepository.save(questioning);

        return questioning;
    }

    @Test
    @DisplayName("Should set questioning accreditation as main and create contact event")
    void shouldSetQuestioningAccreditationAsMain() {
        Questioning questioning = createAndRegisterQuestioning();
        Contact contact = createAndSaveContact("contact-main-test");
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");
        QuestioningAccreditation qa = new QuestioningAccreditation();
        qa.setId(100L);
        qa.setQuestioning(questioning);
        qa.setMain(false); // initially not main
        qa.setIdContact(contact.getIdentifier());
        accreditationRepo.save(qa);

        service.setQuestioningAccreditationAsMain(qa, contact, payload);

        Optional<QuestioningAccreditation> saved = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getIdContact()).isEqualTo(contact.getIdentifier());
        assertThat(saved.get().isMain()).isTrue();
        Optional<ContactEvent> event = contactEventService.findContactEventsByContact(contact).stream().findFirst();
        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(event.get().getPayload()).isEqualTo(payload);
    }

    @Test
    @DisplayName("Should not update accreditation or log events if contact is unchanged")
    void shouldNotUpdateAccreditationIfContactIsUnchanged() {
        Questioning questioning = createAndRegisterQuestioning();
        Contact contact = createAndSaveContact("same-contact");
        Campaign campaign = getCampaignFromPartition();
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");
        QuestioningAccreditation qa = new QuestioningAccreditation();
        qa.setId(999L);
        qa.setQuestioning(questioning);
        qa.setMain(true);
        qa.setIdContact(contact.getIdentifier());
        accreditationRepo.save(qa);

        service.updateExistingMainAccreditationToNewContact(qa, contact, questioning.getSurveyUnit().getIdSu(), payload, campaign);

        assertThat(contactEventService.findContactEventsByContact(contact)).isEmpty();
        Optional<QuestioningAccreditation> result = accreditationRepo.findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getIdContact()).isEqualTo(contact.getIdentifier());
    }

    @Test
    @DisplayName("Should update existing secondary accreditation instead of creating a new one")
    void shouldUpdateExistingSecondaryAccreditation() {
        Questioning questioning = createAndRegisterQuestioning();
        Contact contact = createAndSaveContact("secondary-contact");
        Campaign campaign = getCampaignFromPartition();
        JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");
        Date now = new Date();
        QuestioningAccreditation existing = new QuestioningAccreditation();
        existing.setId(200L);
        existing.setIdContact(contact.getIdentifier());
        existing.setMain(false);
        existing.setCreationDate(new Date(now.getTime() - 100000));
        existing.setQuestioning(questioning);
        accreditationRepo.save(existing);

        service.createQuestioningAccreditation(questioning, true, contact, payload, now, campaign);

        Optional<QuestioningAccreditation> result = accreditationRepo
                .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getIdContact()).isEqualTo(contact.getIdentifier());
        assertThat(result.get().isMain()).isTrue();
        assertThat(result.get().getId()).isEqualTo(existing.getId()); // should reuse the same object
        Optional<ContactEvent> event = contactEventService.findContactEventsByContact(contact).stream().findFirst();
        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(ContactEventTypeEnum.update);
        assertThat(event.get().getPayload()).isEqualTo(payload);
    }


}
