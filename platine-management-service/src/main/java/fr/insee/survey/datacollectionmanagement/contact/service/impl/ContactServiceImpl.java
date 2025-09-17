package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.*;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.ldap.service.LdapService;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignStatusDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.util.ServiceJsonUtil;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final AddressService addressService;

    private final ContactEventService contactEventService;

    private final ViewService viewService;

    private final ModelMapper modelMapper;

    private final CampaignService campaignService;

    private final LdapService ldapService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final QuestioningRepository questioningRepository;


    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact findByIdentifier(String identifier) {
        return contactRepository.findById(identifier).orElseThrow(() -> new NotFoundException(String.format("Contact %s not found", identifier)));
    }

    @Override
    public ContactDto update(ContactDto contactDto, JsonNode payload) {
        Contact existingContact = contactRepository.findById(contactDto.getIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Contact %s not found", contactDto.getIdentifier())));

        existingContact.setExternalId(contactDto.getExternalId());
        existingContact.setFirstName(contactDto.getFirstName());
        existingContact.setLastName(contactDto.getLastName());
        existingContact.setEmail(contactDto.getEmail());
        existingContact.setFunction(contactDto.getFunction());
        existingContact.setPhone(contactDto.getPhone());
        existingContact.setOtherPhone(contactDto.getOtherPhone());
        existingContact.setUsualCompanyName(contactDto.getUsualCompanyName());
        if (contactDto.getCivility() != null) {
            existingContact.setGender(GenderEnum.fromStringIgnoreCase(contactDto.getCivility()));
        }
        AddressDto addressDto = contactDto.getAddress();

        if (addressDto != null) {
            Address existingAddress = existingContact.getAddress();
            Long existingAddressId = existingAddress != null ? existingAddress.getId() : null;
            addressDto.setId(existingAddressId);
            AddressDto updatedAddress = addressService.updateOrCreateAddress(addressDto);
            existingContact.setAddress(modelMapper.map(updatedAddress, Address.class));
        }
        Contact savedContact = contactRepository.save(existingContact);

        ContactEvent contactEventUpdate = contactEventService.createContactEvent(savedContact, ContactEventTypeEnum.update,
                payload);
        contactEventService.saveContactEvent(contactEventUpdate);

        return modelMapper.map(savedContact, ContactDto.class);
    }

    @Override
    public boolean existsByIdentifier(String identifier) {
        return contactRepository.existsById(identifier);
    }

    @Override
    public List<QuestioningContactDto> findByIdentifiers(Map<String, Boolean> mapContactsIdMain) {
        List<Contact> contacts = contactRepository.findAllById(mapContactsIdMain.keySet());
        return contacts.stream()
                .map(contact -> new QuestioningContactDto(contact.getIdentifier(), contact.getLastName(), contact.getFirstName(), mapContactsIdMain.get(contact.getIdentifier())))
                .toList();
    }

    @Override
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public void deleteContact(String identifier) {
        contactRepository.deleteById(identifier);
    }

    @Override
    @Transactional
    public Contact updateOrCreateContact(String id, ContactDto contactDto, JsonNode payload) {
        Optional<Contact> existingContact = contactRepository.findById(id);

        if (existingContact.isPresent()) {
            Contact contact = convertToEntity(contactDto);
            if (contactDto.getAddress() != null) {
                contact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
            }
            return updateContactAddressEvent(contact, payload);

        }
        Contact newContact = convertToEntityNewContact(contactDto);

        if (contactDto.getAddress() != null) {
            newContact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
        }

        Contact createdContact = createAddressAndEvent(newContact, payload);

        viewService.createViewAndDeleteEmptyExistingOnesByIdentifier(id, null, null);

        return createdContact;

    }


    @Override
    public Page<SearchContactDto> searchContactByIdentifier(String identifier, Pageable pageable) {
        return contactRepository.findByIdentifier(identifier, pageable);
    }

    @Override
    public Page<SearchContactDto> searchContactByEmail(String email, Pageable pageable) {
        return contactRepository.findByEmail(email, pageable);
    }

    @Override
    public Page<SearchContactDto> searchContactByName(String name, Pageable pageable) {
        return contactRepository.findByFirstNameLastName(name, pageable);
    }


    @Override
    public Contact createAddressAndEvent(Contact contact, JsonNode payload) {
        if (contact.getAddress() != null) {
            addressService.saveAddress(contact.getAddress());
        }
        Contact contactS = saveContact(contact);
        ContactEvent newContactEvent = contactEventService.createContactEvent(contactS, ContactEventTypeEnum.create,
                payload);
        contactEventService.saveContactEvent(newContactEvent);
        return contactS;
    }

    @Override
    public Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException {

        Contact existingContact = findByIdentifier(contact.getIdentifier());
        if (contact.getAddress() != null) {
            if (existingContact.getAddress() != null) {
                contact.getAddress().setId(existingContact.getAddress().getId());
            }
            addressService.saveAddress(contact.getAddress());
        }

        ContactEvent contactEventUpdate = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update,
                payload);
        contactEventService.saveContactEvent(contactEventUpdate);
        return saveContact(contact);
    }

    @Override
    public void deleteContactAddressEvent(Contact contact) {
        // delete cascade
        deleteContact(contact.getIdentifier());


    }

    @Override
    public ContactDto convertToDto(Contact contact) {
        ContactDto contactDto = modelMapper.map(contact, ContactDto.class);
        contactDto.setCivility(contact.getGender().name());
        return contactDto;
    }

    @Override
    public Contact convertToEntity(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(GenderEnum.fromStringIgnoreCase(contactDto.getCivility()));
        Contact oldContact = findByIdentifier(contactDto.getIdentifier());
        contact.setComment(oldContact.getComment());
        contact.setAddress(oldContact.getAddress());
        contact.setContactEvents(oldContact.getContactEvents());

        return contact;
    }

    @Override
    public Contact convertToEntityNewContact(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(GenderEnum.fromStringIgnoreCase(contactDto.getCivility()));
        return contact;
    }

    @Override
    public ContactDetailsDto getContactDetails(String idContact) {
        Contact contact = findByIdentifier(idContact);
        List<String> listCampaigns = viewService.findDistinctCampaignByIdentifier(idContact);
        List<CampaignStatusDto> campaignsStatus = campaignService.findCampaignStatusByCampaignIdIn(listCampaigns);
        ContactDetailsDto contactDetailsDto = modelMapper.map(contact, ContactDetailsDto.class);
        contactDetailsDto.setCivility(contact.getGender());
        contactDetailsDto.setListCampaigns(campaignsStatus);
        return contactDetailsDto;
    }

    @Override
    public ContactDto createContactAndAssignToAccreditationAsMain(UUID questioningId, ContactDto contact) {
        if(!questioningRepository.existsById(questioningId))
        {
            throw new NotFoundException(String.format("Missing Questioning with id %s", questioningId));
        }

        ContactDto ldapAddedContactDto = createAndSaveContact(contact);
        saveContactCreationEvent(ldapAddedContactDto.getIdentifier());
        assignMainContactToQuestioning(ldapAddedContactDto.getIdentifier(), questioningId);
        return ldapAddedContactDto;
    }

    @Override
    public ContactDto createAndSaveContact(ContactDto contactDto) {
        ContactDto ldapContact = ldapService.createUser(contactDto);
        contactRepository.save(modelMapper.map(ldapContact, Contact.class));
        return ldapContact;
    }

    @Override
    public void saveContactCreationEvent(String contactId) {
        ContactEventDto contactEventDto = new ContactEventDto();
        contactEventDto.setEventDate(new Date());
        contactEventDto.setType(ContactEventTypeEnum.create.toString());
        contactEventDto.setPayload(ServiceJsonUtil.createPayload("platine-pilotage"));
        contactEventDto.setIdentifier(contactId);
        contactEventService.addContactEvent(contactEventDto);
    }

    @Override
    public void assignMainContactToQuestioning(String contactIdentifier, UUID questioningId) {
        questioningAccreditationService.setMainQuestioningAccreditationToContact(contactIdentifier, questioningId);
    }
}
