package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ContactEventServiceImplTest {

    private static final Long CONTACT_EVENT_ID = 1L;
    @Mock
    private ContactEventRepository contactEventRepository;
    @InjectMocks
    private ContactEventServiceImpl contactEventService;
    private ContactEvent contactEvent;

    @BeforeEach
    public void setUp() {
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
}