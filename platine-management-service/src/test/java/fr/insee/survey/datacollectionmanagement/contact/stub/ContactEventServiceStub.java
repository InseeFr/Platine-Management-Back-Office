package fr.insee.survey.datacollectionmanagement.contact.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public class ContactEventServiceStub implements ContactEventService {

    List<ContactEvent> contactEvents = List.of();

    @Override
    public Page<ContactEvent> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public ContactEvent findById(Long id) {
        return contactEvents.stream().filter(contactEvent -> contactEvent.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException(String.format("error")));
    }

    @Override
    public ContactEvent saveContactEvent(ContactEvent contactEvent) {
        contactEvents.add(contactEvent);
        return  contactEvent;
    }

    @Override
    public void deleteContactEvent(Long id) {
        contactEvents.remove(findById(id));
    }

    @Override
    public Set<ContactEvent> findContactEventsByContact(Contact contact) {
        return Set.of();
    }

    @Override
    public ContactEvent createContactEvent(Contact contact, ContactEventTypeEnum type, JsonNode payload) {
        return null;
    }

    @Override
    public ContactEventDto addContactEvent(ContactEventDto contactEventDto) {
        return null;
    }

    @Override
    public List<ContactEventDto> findContactEventsByContactId(String contactId) {
        return List.of();
    }
}
