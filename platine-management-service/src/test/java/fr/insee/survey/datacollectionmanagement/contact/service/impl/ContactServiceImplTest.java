package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.stub.AdressServiceStub;
import fr.insee.survey.datacollectionmanagement.contact.stub.ContactEventServiceStub;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.ldap.service.stub.LdapServiceStub;
import fr.insee.survey.datacollectionmanagement.ldap.service.LdapService;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ContactRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ContactServiceImplTest {

    private ContactRepositoryStub contactRepository;
    private ContactService contactService;
    private AddressService addressService;
    private ContactEventService contactEventService;
    private LdapService ldapService;
    private ViewServiceStub viewService;
    private CampaignServiceStub campaignService;
    private QuestioningAccreditationService questioningAccreditationService;
    private QuestioningRepository questioningRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void init() {
        contactRepository = new ContactRepositoryStub();
        addressService = new AdressServiceStub(modelMapper);
        contactEventService = new ContactEventServiceStub(modelMapper);
        viewService = new ViewServiceStub();
        campaignService = new CampaignServiceStub();
        ldapService = new LdapServiceStub();
        questioningAccreditationService = new QuestioningAccreditationServiceStub();
        questioningRepository = new QuestioningRepositoryStub();

        contactService = new ContactServiceImpl(
                contactRepository,
                addressService,
                contactEventService,
                viewService,
                modelMapper,
                campaignService,
                ldapService,
                questioningAccreditationService,
                questioningRepository);
    }

    @Test
    void getContactDetails_shouldReturnDetails_whenContactExists() {
        String contactId = "123";
        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contact.setGender(GenderEnum.Female);
        contact.setLastName("Smith");

        contactRepository.save(contact);
        viewService.addCampaignForContact(contactId, "CAMP1");
        campaignService.addCampaignStatus("CAMP1", CollectionStatus.OPEN);

        ContactDetailsDto result = contactService.getContactDetails(contactId);

        assertNotNull(result);
        assertEquals("123", result.getIdentifier());
        assertEquals(GenderEnum.Female, result.getCivility());
        assertEquals(1, result.getListCampaigns().size());
        assertEquals("CAMP1", result.getListCampaigns().get(0).id());
    }

    @Test
    void getContactDetails_shouldThrowException_whenContactNotFound() {
        String contactId = "999";
        assertThrows(NotFoundException.class, () -> contactService.getContactDetails(contactId));
    }

    @Test
    void getContactDetails_shouldReturnEmptyList_whenNoCampaignsFound() {
        String contactId = "123";
        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contact.setGender(GenderEnum.Male);

        contactRepository.save(contact);
        viewService.setCampaignsByIdentifiers(Collections.emptyMap());

        ContactDetailsDto result = contactService.getContactDetails(contactId);

        assertNotNull(result);
        assertEquals(0, result.getListCampaigns().size());
    }

    @Test
    void shouldFindByIdentifiers() {
        Contact contact = new Contact();
        contact.setIdentifier("id1");
        contact.setLastName("Smith");
        contact.setFirstName("John");
        contactRepository.save(contact);
        Contact contact2 = new Contact();
        contact2.setIdentifier("id2");
        contact2.setLastName("Doe");
        contact2.setFirstName("Jane");
        contactRepository.save(contact2);

        Map<String, Boolean> mapContactIdentifierMain = Map.of(
                "id1", true,
                "id2", false
        );

        List<QuestioningContactDto> result = contactService.findByIdentifiers(mapContactIdentifierMain);


        assertThat(result).hasSize(2);
        assertThat(result).extracting(QuestioningContactDto::identifier).containsExactlyInAnyOrder("id1", "id2");
        assertThat(result).extracting(QuestioningContactDto::lastName).containsExactlyInAnyOrder("Smith", "Doe");
        assertThat(result).extracting(QuestioningContactDto::firstName).containsExactlyInAnyOrder("Jane", "John");
        assertThat(result).extracting(QuestioningContactDto::isMain).containsExactlyInAnyOrder(true, false);

    }

    @Test
    void shouldReturnEmptyListWhenNoIdentifiersMatch() {
        List<QuestioningContactDto> result = contactService.findByIdentifiers(Map.of("id3", false));
        assertThat(result).isEmpty();
    }

    @Test
    void existsByIdentifier_shouldReturnTrueIfContactExists() {
        // GIVEN
        Contact contact = new Contact();
        contact.setIdentifier("contact-123");
        contactRepository.save(contact);

        // WHEN
        boolean exists = contactService.existsByIdentifier("contact-123");

        // THEN
        assertTrue(exists);
    }

    @Test
    void existsByIdentifier_shouldReturnFalseIfContactDoesNotExist() {
        // WHEN
        boolean exists = contactService.existsByIdentifier("unknown-id");

        // THEN
        assertFalse(exists);
    }

    @Test
    void update_shouldUpdateContactAndReturnUpdatedDto() {
        // GIVEN
        Contact contact = new Contact();
        contact.setIdentifier("id-999");
        contact.setGender(GenderEnum.Male);
        contactRepository.save(contact);

        ContactDto contactDto = new ContactDto();
        contactDto.setIdentifier("id-999");
        contactDto.setFirstName("John");
        contactDto.setLastName("Doe");
        contactDto.setEmail("john.doe@example.com");
        contactDto.setCivility("Male");

        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("rue des Lilas");
        contactDto.setAddress(addressDto);

        // WHEN
        ContactDto result = contactService.update(contactDto, null);

        // THEN
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should create contact and assign accreditation")
    void shouldCreateContactAndAssignAccreditation() {
        UUID questioningId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");
        ContactDto inputContact = new ContactDto();
        inputContact.setEmail("john.doe@example.com");

        Questioning questioning = new Questioning();
        questioning.setId(questioningId);
        questioningRepository.save(questioning);

        ContactDto result = contactService.createContactAndAssignToAccreditationAsMain(questioningId, inputContact);

        assertThat(result).isNotNull();

        // LdapService stub will create a contact with "Id" as identifier
        assertThat(result.getIdentifier()).isEqualTo("Id");
        Contact storedContact = contactRepository.findById("Id").orElse(null);
        assertThat(storedContact).isNotNull();
        assertThat(storedContact.getIdentifier()).isEqualTo("Id");
        assertThat(storedContact.getEmail()).isEqualTo("john.doe@example.com");

        List<ContactEventDto> contactEvents = contactEventService.findContactEventsByContactId("Id");
        assertThat(contactEvents).hasSize(1);
        assertThat(contactEvents.getFirst().getType()).isEqualTo(ContactEventTypeEnum.create.toString());
    }

    @Test
    @DisplayName("Should throw NotFoundException when questioning is missing")
    void shouldThrowWhenQuestioningNotFound() {
        UUID invalidQuestioningId = UUID.randomUUID();
        ContactDto inputContact = new ContactDto();
        inputContact.setIdentifier("jane.doe");
        inputContact.setEmail("jane.doe@example.com");

        assertThatThrownBy(() ->
                contactService.createContactAndAssignToAccreditationAsMain(invalidQuestioningId, inputContact))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Missing Questioning with id " + invalidQuestioningId);
    }
}

