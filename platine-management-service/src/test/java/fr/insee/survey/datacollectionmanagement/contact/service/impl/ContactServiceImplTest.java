package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

        List<QuestioningContactDto> result = contactService.findByIdentifiers(List.of("id1", "id2"));


        assertThat(result).hasSize(2);
        assertThat(result).extracting(QuestioningContactDto::identifier).containsExactlyInAnyOrder("id1", "id2");
        assertThat(result).extracting(QuestioningContactDto::lastName).containsExactlyInAnyOrder("Smith", "Doe");
        assertThat(result).extracting(QuestioningContactDto::firstName).containsExactlyInAnyOrder("Jane", "John");
    }

    @Test
    void shouldReturnEmptyListWhenNoIdentifiersMatch() {
        List<QuestioningContactDto> result = contactService.findByIdentifiers(List.of("id3"));
        assertThat(result).isEmpty();
    }
}

