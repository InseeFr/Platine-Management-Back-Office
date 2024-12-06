package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final AddressService addressService;

    private final ContactEventService contactEventService;

    private final ViewService viewService;

    private final ModelMapper modelMapper;

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
        // Vérifier si le contact existe
        Optional<Contact> existingContact = contactRepository.findById(id);

        if (existingContact.isPresent()) {
            // Mise à jour du contact existant
            Contact contact = convertToEntity(contactDto);

            if (contactDto.getAddress() != null) {
                contact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
            }

            return updateContactAddressEvent(contact, payload);

        } else {
            // Création d'un nouveau contact
            Contact newContact = convertToEntityNewContact(contactDto);

            if (contactDto.getAddress() != null) {
                newContact.setAddress(addressService.convertToEntity(contactDto.getAddress()));
            }

            Contact createdContact = createContactAddressEvent(newContact, payload);

            // Créer une vue pour le nouveau contact
            viewService.createView(id, null, null);

            return createdContact;
        }
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
    public Page<SearchContactDto> searchContactByParam(String param, Pageable pageable) {
        return contactRepository.findByParam(param, pageable);
    }




    @Override
    public Contact createContactAddressEvent(Contact contact, JsonNode payload) {
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
    public ContactDetailsDto convertToContactDetailsDto(Contact contact, List<String> listCampaigns) {
        ContactDetailsDto contactDetailsDto = modelMapper.map(contact, ContactDetailsDto.class);
        contactDetailsDto.setCivility(contact.getGender());
        contactDetailsDto.setListCampaigns(listCampaigns);
        return contactDetailsDto;
    }

    @Override
    public Contact convertToEntity(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(GenderEnum.valueOf(contactDto.getCivility()));
        Contact oldContact = findByIdentifier(contactDto.getIdentifier());
        contact.setComment(oldContact.getComment());
        contact.setAddress(oldContact.getAddress());
        contact.setContactEvents(oldContact.getContactEvents());

        return contact;
    }

    @Override
    public Contact convertToEntityNewContact(ContactDto contactDto) {
        Contact contact = modelMapper.map(contactDto, Contact.class);
        contact.setGender(GenderEnum.valueOf(contactDto.getCivility()));
        return contact;
    }


}
