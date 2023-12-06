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
     * @param pageable
     * @return contact Page
     */
    public Page<Contact> findAll(Pageable pageable);

    /**
     * Find a contact by its identifier.
     *
     * @param identifier
     * @return contact found
     */
    public Contact findByIdentifier(String identifier) ;

    /**
     * Update an existing contact and its address, or creates a new one
     * 
     * @param contact
     * @return contact updated
     */
    public Contact saveContact(Contact contact);

    /**
     * Delete a contact. Delete also the contact address.
     * @param identifier
     */
    public void deleteContact(String identifier);

    public List<Contact> findByLastName(String lastName);

    public List<Contact> findByFirstName(String firstName);

    public List<Contact> findByEmail(String email);

    public List<Contact> searchListContactParameters(String identifier, String lastName, String firstName,
            String email);

    public Contact createContactAddressEvent(Contact contact, JsonNode payload);

    public Contact updateContactAddressEvent(Contact contact, JsonNode payload) throws NotFoundException;

    public void deleteContactAddressEvent(Contact contact);

}
