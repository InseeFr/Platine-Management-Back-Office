package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.AddressDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.enums.CollectionStatus;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.service.impl.stub.ViewServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ContactRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ContactServiceImplTest {

    private ContactRepositoryStub contactRepository;
    private ContactService contactService;
    private AddressService addressService;
    private ContactEventService contactEventService;
    private ViewServiceStub viewService;
    private CampaignServiceStub campaignService;
    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void init() {
        contactRepository = new ContactRepositoryStub();
        addressService = Mockito.mock(AddressService.class);
        contactEventService = Mockito.mock(ContactEventService.class);
        viewService = new ViewServiceStub();
        campaignService = new CampaignServiceStub();
        contactService = new ContactServiceImpl(contactRepository, addressService,
                contactEventService, viewService, modelMapper, campaignService);
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

        when(addressService.updateOrCreateAddress(any(AddressDto.class))).thenReturn(addressDto);
        when(contactEventService.createContactEvent(any(), eq(ContactEventTypeEnum.update), any()))
                .thenReturn(new ContactEvent());

        // WHEN
        ContactDto result = contactService.update(contactDto, null);

        // THEN
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
    }
}

