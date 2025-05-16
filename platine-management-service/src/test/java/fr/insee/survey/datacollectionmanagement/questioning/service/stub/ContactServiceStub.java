package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContactServiceStub implements ContactService {

    List<Contact> contacts = new ArrayList<>();

    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Contact> findAll() {
        return List.of();
    }

    @Override
    public Contact findByIdentifier(String identifier) {
        Optional<Contact> contact = contacts.stream().filter(c -> c.getIdentifier().equals(identifier)).findFirst();
        return contact.orElseThrow(() -> new NotFoundException(String.format("Contact %s not found", identifier)));
    }

    @Override
    public ContactDto update(ContactDto contactDto, JsonNode payload) {
        return null;
    }

    public boolean existsByIdentifier(String identifier) {
        return contacts.stream().anyMatch(contact -> contact.getIdentifier().equals(identifier));
    }

    @Override
    public List<QuestioningContactDto> findByIdentifiers(Map<String, Boolean> mapContactsIdMain) {
        return List.of();
    }

    @Override
    public Contact saveContact(Contact contact) {
        contacts.add(contact);
        return contact;
    }

    @Override
    public void deleteContact(String identifier) {
        //not used
    }

    @Override
    public Contact updateOrCreateContact(String id, ContactDto contactDto, JsonNode payload) {
        return null;
    }

    @Override
    public Contact createAddressAndEvent(Contact contact, JsonNode payload) {
        return null;
    }

    @Override
    public Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException {
        return null;
    }

    @Override
    public void deleteContactAddressEvent(Contact contact) {
        //not used
    }

    @Override
    public Page<SearchContactDto> searchContactByIdentifier(String identifier, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchContactDto> searchContactByEmail(String email, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchContactDto> searchContactByName(String name, Pageable pageable) {
        return null;
    }

    @Override
    public ContactDto convertToDto(Contact contact) {
        return null;
    }

    @Override
    public Contact convertToEntity(ContactDto contactDto) {
        return null;
    }

    @Override
    public Contact convertToEntityNewContact(ContactDto contactDto) {
        return null;
    }

    @Override
    public ContactDetailsDto getContactDetails(String idContact) {
        return null;
    }
}
