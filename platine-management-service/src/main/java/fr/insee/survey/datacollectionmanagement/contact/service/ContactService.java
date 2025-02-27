package fr.insee.survey.datacollectionmanagement.contact.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDetailsDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactService {

    /**
     * Find all contacts
     *
     * @param pageable pageable
     * @return contact Page
     */
    Page<Contact> findAll(Pageable pageable);

    List<Contact> findAll();

    /**
     * Find a contact by its identifier.
     *
     * @param identifier contact identifier
     * @return contact found
     */
    Contact findByIdentifier(String identifier);

    /**
     * Find all contacts by identifiers list
     *
     * @param identifiers
     * @return
     */
    List<QuestioningContactDto> findByIdentifiers(List<String> identifiers);

    /**
     * Update an existing contact and its address, or creates a new one
     *
     * @param contact Contact to save
     * @return contact updated
     */
    Contact saveContact(Contact contact);

    /**
     * Delete a contact. Delete also the contact address.
     *
     * @param identifier contact identifier
     */
    void deleteContact(String identifier);

    Contact updateOrCreateContact(String id, ContactDto contactDto, JsonNode payload);

    Contact createAddressAndEvent(Contact contact, JsonNode payload);

    Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException;

    void deleteContactAddressEvent(Contact contact);

    Page<SearchContactDto> searchContactByIdentifier(String identifier, Pageable pageable);

    Page<SearchContactDto> searchContactByEmail(String email, Pageable pageable);

    Page<SearchContactDto> searchContactByName(String name, Pageable pageable);


    ContactDto convertToDto(Contact contact);

    ContactDetailsDto convertToContactDetailsDto(Contact contact, List<String> listCampaigns);

    Contact convertToEntity(ContactDto contactDto);

    Contact convertToEntityNewContact(ContactDto contactDto);
}
