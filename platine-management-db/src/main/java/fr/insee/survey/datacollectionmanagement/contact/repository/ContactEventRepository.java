package fr.insee.survey.datacollectionmanagement.contact.repository;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ContactEventRepository extends JpaRepository<ContactEvent, Long> {
    
    Set<ContactEvent> findByContact(Contact contact);
    List<ContactEvent> findByContactIdentifier(String contactId);

}
