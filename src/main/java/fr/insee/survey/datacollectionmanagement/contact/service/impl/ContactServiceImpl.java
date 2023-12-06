package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent.ContactEventType;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
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
    public List<Contact> findByLastName(String lastName) {
        return contactRepository.findByLastNameIgnoreCase(lastName);
    }

    @Override
    public List<Contact> findByFirstName(String firstName) {
        return contactRepository.findByFirstNameIgnoreCase(firstName);
    }

    @Override
    public List<Contact> findByEmail(String email) {
        return contactRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public List<Contact> searchListContactParameters(String identifier, String lastName, String firstName,
                                                     String email) {

        List<Contact> listContactContact = new ArrayList<>();
        boolean alwaysEmpty = true;

        if (!StringUtils.isEmpty(identifier)) {
            listContactContact = Arrays.asList(findByIdentifier(identifier));
            alwaysEmpty = false;
        }

        if (!StringUtils.isEmpty(lastName)) {
            if (listContactContact.isEmpty() && alwaysEmpty) {
                listContactContact.addAll(findByLastName(lastName));
                alwaysEmpty = false;
            } else
                listContactContact = listContactContact.stream().filter(c -> c.getLastName().equalsIgnoreCase(lastName))
                        .toList();

        }

        if (!StringUtils.isEmpty(firstName)) {
            if (listContactContact.isEmpty() && alwaysEmpty) {
                listContactContact.addAll(findByFirstName(firstName));
                alwaysEmpty = false;
            } else
                listContactContact = listContactContact.stream()
                        .filter(c -> c.getFirstName().equalsIgnoreCase(firstName)).toList();
        }

        if (!StringUtils.isEmpty(email)) {
            if (listContactContact.isEmpty() && alwaysEmpty) {
                listContactContact.addAll(findByEmail(email));
                alwaysEmpty = false;
            } else
                listContactContact = listContactContact.stream().filter(c -> c.getEmail().equalsIgnoreCase(email))
                        .toList();
        }

        return listContactContact;
    }

    @Override
    @Transactional
    public Contact createContactAddressEvent(Contact contact, JsonNode payload) {
        if (contact.getAddress() != null) {
            addressService.saveAddress(contact.getAddress());
        }
        ContactEvent newContactEvent = contactEventService.createContactEvent(contact, ContactEventType.create,
                payload);
        contact.setContactEvents(new HashSet<>(Arrays.asList(newContactEvent)));
        return saveContact(contact);
    }

    @Override
    @Transactional
    public Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException {

        Contact existingContact = findByIdentifier(contact.getIdentifier());
        if (contact.getAddress() != null) {
            if (existingContact.getAddress() != null) {
                contact.getAddress().setId(existingContact.getAddress().getId());
            }
            addressService.saveAddress(contact.getAddress());
        }

        Set<ContactEvent> setContactEventsContact = existingContact.getContactEvents();
        ContactEvent contactEventUpdate = contactEventService.createContactEvent(contact, ContactEventType.update,
                payload);
        setContactEventsContact.add(contactEventUpdate);
        contact.setContactEvents(setContactEventsContact);
        return saveContact(contact);
    }

    @Override
    public void deleteContactAddressEvent(Contact contact) {
        // delete cascade
        deleteContact(contact.getIdentifier());

    }

}
