package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ValidatedQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestioningEventServiceImplTest {

    @Mock
    private QuestioningEventRepository questioningEventRepository;

    @Mock
    private QuestioningRepository questioningRepository;

    @InjectMocks
    private QuestioningEventServiceImpl questioningEventService;

    private ValidatedQuestioningEventDto validatedDto;
    private Questioning questioning;
    private QuestioningEvent existingEvent;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        validatedDto = new ValidatedQuestioningEventDto();
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

    private JsonNode createPayload() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{ \"source\": \"test\" }";
        return objectMapper.readTree(jsonString);
    }

    @Test
    @DisplayName("Should throw NotFoundException when questioning does not exist")
    void postValintQuestioningEventTest() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questioningEventService.postValintQuestioningEvent(validatedDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Questioning 1 does not exist");
    }

    @Test
    @DisplayName("Should throw TooManyValuesException when multiple VALINT events exist")
    void postValintQuestioningEventTest2() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of(new QuestioningEvent(), new QuestioningEvent()));

        assertThatThrownBy(() -> questioningEventService.postValintQuestioningEvent(validatedDto))
                .isInstanceOf(TooManyValuesException.class)
                .hasMessageContaining("2 VALINT questioningEvents found");
    }

    @Test
    @DisplayName("Should update existing VALINT event when one exists")
    void postValintQuestioningEventTest3() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of(existingEvent));

        boolean result = questioningEventService.postValintQuestioningEvent(validatedDto);

        assertThat(result).isFalse();
        assertThat(existingEvent.getDate()).isEqualTo(validatedDto.getDate());
        assertThat(existingEvent.getPayload()).isEqualTo(validatedDto.getPayload());

        verify(questioningEventRepository).save(existingEvent);
    }

    @Test
    @DisplayName("Should create new VALINT event when none exist")
    void postValintQuestioningEventTest4() {
        when(questioningRepository.findById(1L)).thenReturn(Optional.of(questioning));
        when(questioningEventRepository.findByQuestioningIdAndType(1L, TypeQuestioningEvent.VALINT))
                .thenReturn(List.of());

        boolean result = questioningEventService.postValintQuestioningEvent(validatedDto);

        assertThat(result).isTrue();
        verify(questioningEventRepository).save(any(QuestioningEvent.class));
    }

    @Test
    @DisplayName("Should return false when no questioning event types match")
    void containsQuestioningEventsTest() {
        Set<QuestioningEvent> events = new HashSet<>();

        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);

        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        boolean result = questioningEventService.containsQuestioningEvents(questioning, List.of());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when questioning event matches given types")
    void containsQuestioningEventsTest2() {
        Set<QuestioningEvent> events = new HashSet<>();

        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);

        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        boolean result = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when questioning events do not match given types")
    void containsQuestioningEventsTest3() {
        Set<QuestioningEvent> events = new HashSet<>();

        QuestioningEvent questioningEventValid = new QuestioningEvent();
        questioningEventValid.setType(TypeQuestioningEvent.VALINT);

        QuestioningEvent questioningEventRefused = new QuestioningEvent();
        questioningEventRefused.setType(TypeQuestioningEvent.REFUSAL);

        events.add(questioningEventValid);
        events.add(questioningEventRefused);
        questioning.setQuestioningEvents(events);

        boolean result = questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS);

        assertThat(result).isFalse();
    }
}
