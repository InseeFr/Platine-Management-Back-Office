package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContactEventServiceImpl implements ContactEventService {

    private final ContactEventRepository contactEventRepository;

    @Override
    public Page<ContactEvent> findAll(Pageable pageable) {
        return contactEventRepository.findAll(pageable);
    }

    @Override
    public ContactEvent findById(Long id) {
        return contactEventRepository.findById(id).orElseThrow(()->new NotFoundException(String.format("ContactEvent %s not found", id)));
    }

    @Override
    public ContactEvent saveContactEvent(ContactEvent contactEvent) {
        return contactEventRepository.save(contactEvent);
    }

    @Override
    public void deleteContactEvent(Long id) {
        contactEventRepository.deleteById(id);
    }

    @Override
    public Set<ContactEvent> findContactEventsByContact(Contact contact) {
        return contactEventRepository.findByContact(contact);
    }

    @Override
    public ContactEvent createContactEvent(Contact contact, ContactEventTypeEnum type, JsonNode payload) {
        ContactEvent contactEventCreate = new ContactEvent();
        contactEventCreate.setContact(contact);
        contactEventCreate.setType(type);
        contactEventCreate.setPayload(payload);
        contactEventCreate.setEventDate(new Date());
        return contactEventCreate;
    }
}
