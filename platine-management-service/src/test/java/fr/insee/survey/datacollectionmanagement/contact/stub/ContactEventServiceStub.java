package fr.insee.survey.datacollectionmanagement.contact.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@RequiredArgsConstructor
public class ContactEventServiceStub implements ContactEventService {

    ArrayList<ContactEvent> contactEvents = new ArrayList<>();

    private final ModelMapper modelMapper;


    @Override
    public Page<ContactEvent> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public ContactEvent findById(Long id) {
        return contactEvents.stream().filter(contactEvent -> contactEvent.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException(String.format("ContactEvent not found for %s", id)));
    }

    @Override
    public ContactEvent saveContactEvent(ContactEvent contactEvent) {
        contactEvents.add(contactEvent);
        return  contactEvent;
    }

    @Override
    public Set<ContactEvent> findContactEventsByContact(Contact contact) {
         List<ContactEvent> contactEventList = contactEvents.stream().filter(contactEvent -> contactEvent.getContact()
                 .getIdentifier().equals(contact.getIdentifier())).toList();
         return new HashSet<>(contactEventList);
    }

    @Override
    public ContactEvent createContactEvent(Contact contact, ContactEventTypeEnum type, JsonNode payload) {
        ContactEvent contactEvent = new ContactEvent();
        contactEvent.setContact(contact);
        contactEvent.setType(type);
        contactEvent.setPayload(payload);
        contactEvent.setEventDate(new Date());
        contactEvents.add(contactEvent);
        return contactEvent;
    }

    @Override
    public ContactEventDto addContactEvent(ContactEventDto contactEventDto) {
         contactEvents.add(modelMapper.map(contactEventDto, ContactEvent.class));
         return  contactEventDto;
    }

    @Override
    public List<ContactEventDto> findContactEventsByContactId(String contactId) {
        return contactEvents.stream().filter(contactEvent -> contactEvent.getContact().getIdentifier().equals(contactId))
                .map(contactEvent ->  modelMapper.map(contactEvent, ContactEventDto.class)).toList();
    }
}
