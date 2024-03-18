package fr.insee.survey.datacollectionmanagement.contact.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
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
    Contact findByIdentifier(String identifier) ;

    /**
     * Update an existing contact and its address, or creates a new one
     *
     * @param contact Contact to save
     * @return contact updated
     */
    Contact saveContact(Contact contact);

    /**
     * Delete a contact. Delete also the contact address.
     * @param identifier contact identifier
     */
    void deleteContact(String identifier);

    Page<Contact> findByParameters(String identifier, String name, String email, Pageable pageable);

    Contact createContactAddressEvent(Contact contact, JsonNode payload);

    Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException;

    void deleteContactAddressEvent(Contact contact);

}
