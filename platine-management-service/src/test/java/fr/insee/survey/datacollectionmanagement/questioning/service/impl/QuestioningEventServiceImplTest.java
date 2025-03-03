package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.LastQuestioningEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestioningEventServiceImplTest {

    @Mock
    private LastQuestioningEventComparator lastQuestioningEventComparator;
    @Mock
    private QuestioningEventRepository questioningEventRepository;
    @Mock
    private QuestioningRepository questioningRepository;

    @InjectMocks
    private QuestioningEventServiceImpl questioningEventService;

    private QuestioningEventInputDto validatedDto;
    private Questioning questioning;
    private QuestioningEvent existingEvent;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        validatedDto = new QuestioningEventInputDto();
        validatedDto.setQuestioningId(1L);
        validatedDto.setDate(Date.from(Instant.now()));
        validatedDto.setPayload(createPayload());

        questioning = new Questioning();
        questioning.setId(1L);

        existingEvent = new QuestioningEvent();
        existingEvent.setId(100L);
        existingEvent.setQuestioning(questioning);
        existingEvent.setType(TypeQuestioningEvent.VALINT);
    }

    @Test
    void shouldThrowNotFoundException_whenQuestioningDoesNotExist() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                questioningEventService.postValintQuestioningEvent("eventType", validatedDto)
        );

        assertEquals("Questioning 1 does not exist", exception.getMessage());
    }

    @Test
    void shouldThrowTooManyValuesException_whenMultipleVALINTEventsExist() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of(new QuestioningEvent(), new QuestioningEvent()));

        String valintEvent = TypeQuestioningEvent.VALINT.name();
        TooManyValuesException exception = assertThrows(TooManyValuesException.class, () ->
                questioningEventService.postValintQuestioningEvent(valintEvent, validatedDto)
        );

        assertTrue(exception.getMessage().contains("2 VALINT questioningEvents found"));
    }

    @Test
    void shouldDoNothingVALINTEvent_whenOneExists() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of(existingEvent));
        Date dateExistingEvent = existingEvent.getDate();
        String valintEvent = TypeQuestioningEvent.VALINT.name();
        boolean result = questioningEventService.postValintQuestioningEvent(valintEvent, validatedDto);

        assertFalse(result);
        assertEquals(dateExistingEvent, existingEvent.getDate());
    }

    @Test
    void shouldCreateNewVALINTEvent_whenNoneExists() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of());
        String valintEvent = TypeQuestioningEvent.VALINT.name();

        boolean result = questioningEventService.postValintQuestioningEvent(valintEvent, validatedDto);

        assertTrue(result);
        verify(questioningEventRepository).save(any(QuestioningEvent.class));
    }


    private JsonNode createPayload() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{ \"source\": \"test\"}";
        return objectMapper.readTree(jsonString);
    }



}