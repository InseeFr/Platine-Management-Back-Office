package fr.insee.survey.datacollectionmanagement.contact.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ContactEventService {

    public Page<ContactEvent> findAll(Pageable pageable);

    public ContactEvent findById(Long id);

    public ContactEvent saveContactEvent(ContactEvent contactEvent);

    public void deleteContactEvent(Long id);
    
    public Set<ContactEvent> findContactEventsByContact (Contact contact);

    ContactEvent createContactEvent(Contact contact, ContactEventTypeEnum type, JsonNode payload);

    ContactEventDto addContactEvent(ContactEventDto contactEventDto);

    List<ContactEventDto> findContactEventsByContactId(String contactId);
}
