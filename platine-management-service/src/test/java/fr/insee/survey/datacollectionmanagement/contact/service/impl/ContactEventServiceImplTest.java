package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactEventDto;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ContactEventServiceImplTest {

    private static final Long CONTACT_EVENT_ID = 1L;
    @Mock
    private ContactEventRepository contactEventRepository;
    private ModelMapper modelMapper;
    private ContactEventServiceImpl contactEventService;
    private ContactEvent contactEvent;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.typeMap(ContactEvent.class, ContactEventDto.class)
                .addMapping(src -> src.getContact().getIdentifier(), ContactEventDto::setIdentifier);
        contactEventService = new ContactEventServiceImpl(contactEventRepository, modelMapper);
        contactEvent = new ContactEvent();
        contactEvent.setId(CONTACT_EVENT_ID);
    }

    @Test
    void testFindById_should_returnExpectedContactEvent_when_idExists() {
        //Given
        when(contactEventRepository.findById(CONTACT_EVENT_ID)).thenReturn(Optional.of(contactEvent));

        //When
        ContactEvent result = contactEventService.findById(CONTACT_EVENT_ID);

        //Then
        assertNotNull(result);
        assertEquals(contactEvent, result);
        verify(contactEventRepository, times(1)).findById(CONTACT_EVENT_ID);
    }

    @Test
    void testFindById_should_Throw_when_idNotExists() {
        //Given
        Long id = 2L;
        when(contactEventRepository.findById(id)).thenReturn(Optional.empty());

        //When + Then
        NotFoundException resException = assertThrows(NotFoundException.class, () -> contactEventService.findById(id));
        assertEquals("ContactEvent 2 not found", resException.getMessage());
        verify(contactEventRepository, times(1)).findById(id);
    }

    @Test
    void testAddContactEvent_shouldMapAndSaveAndReturnDto() {
        // GIVEN
        ContactEventDto dto = new ContactEventDto();
        dto.setIdentifier("contact-1");

        Contact contact = new Contact();
        contact.setIdentifier("contact-1");

        ContactEvent event = new ContactEvent();
        event.setContact(contact);

        when(contactEventRepository.save(any(ContactEvent.class))).thenAnswer(invocation -> {
            ContactEvent arg = invocation.getArgument(0);
            arg.setId(123L);
            return arg;
        });

        // WHEN
        ContactEventDto result = contactEventService.addContactEvent(dto);

        // THEN
        assertNotNull(result);
        assertEquals("contact-1", result.getIdentifier());
    }

    @Test
    void testFindContactEventsByContactId_shouldReturnMappedDtoList() {
        // GIVEN
        String contactId = "contact-42";

        Contact contact = new Contact();
        contact.setIdentifier(contactId);

        ContactEvent event1 = new ContactEvent();
        event1.setId(1L);
        event1.setContact(contact);

        ContactEvent event2 = new ContactEvent();
        event2.setId(2L);
        event2.setContact(contact);

        List<ContactEvent> events = List.of(event1, event2);
        when(contactEventRepository.findByContactIdentifier(contactId)).thenReturn(events);

        // WHEN
        List<ContactEventDto> result = contactEventService.findContactEventsByContactId(contactId);

        // THEN
        assertEquals(2, result.size());
        assertEquals(contactId, result.get(0).getIdentifier());
        assertEquals(contactId, result.get(1).getIdentifier());
    }
}