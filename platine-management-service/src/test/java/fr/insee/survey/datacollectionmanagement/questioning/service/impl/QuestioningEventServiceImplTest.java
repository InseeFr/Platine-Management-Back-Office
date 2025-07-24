package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestioningEventServiceImplTest {

    private QuestioningEventRepositoryStub questioningEventRepository;

    private QuestioningRepositoryStub questioningRepository;

    private QuestioningEventServiceImpl questioningEventService;

    @BeforeEach
    void setUp() {
        questioningEventRepository = new QuestioningEventRepositoryStub();
        questioningRepository = new QuestioningRepositoryStub();
        questioningEventService = new QuestioningEventServiceImpl(
                null,
                questioningEventRepository,
                questioningRepository,
                new ModelMapper());
    }
    private Questioning createQuestioning() {
        Questioning questioning = new Questioning();
        questioning.setId(UUID.randomUUID());
        return questioning;
    }

    private QuestioningEventInputDto createValidedInputDto(UUID questioningId) {
        QuestioningEventInputDto validatedDto = new QuestioningEventInputDto();
        validatedDto.setQuestioningId(questioningId);
        validatedDto.setDate(Date.from(Instant.now()));
        return validatedDto;
    }

    private QuestioningEvent createQuestioningEvent(long id, TypeQuestioningEvent type, Questioning questioning, Clock clock) {
        QuestioningEvent event = new QuestioningEvent();
        event.setId(id);
        event.setQuestioning(questioning);
        event.setType(type);
        event.setDate(Date.from(Instant.now(clock)));
        return event;
    }

    private QuestioningEvent createQuestioningEvent(long id, TypeQuestioningEvent type, Questioning questioning) {
        return createQuestioningEvent(id, type, questioning, Clock.systemUTC());
    }

    @Test
    @DisplayName("Should throw NotFoundException when questioning does not exist")
    void postValintQuestioningEventTest() {
        QuestioningEventInputDto input = createValidedInputDto(UUID.randomUUID());
        assertThatThrownBy(() -> questioningEventService.postQuestioningEvent("eventType", input))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Questioning "+input.getQuestioningId()+" does not exist");
    }

    @Test
    @DisplayName("Should throw TooManyValuesException when multiple VALINT events exist")
    void postValintQuestioningEventTest2() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent event = createQuestioningEvent(1L, TypeQuestioningEvent.VALINT, questioning);
        QuestioningEvent event2 = createQuestioningEvent(2L, TypeQuestioningEvent.VALINT, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        questioning.setQuestioningEvents(Set.of(event, event2));
        questioningEventRepository.save(event);
        questioningEventRepository.save(event2);
        questioningRepository.save(questioning);

        String valintEvent = TypeQuestioningEvent.VALINT.name();
        QuestioningEventInputDto input = createValidedInputDto(questioningId);
        assertThatThrownBy(() -> questioningEventService.postQuestioningEvent(valintEvent, input))
                .isInstanceOf(TooManyValuesException.class)
                .hasMessageContaining("2 VALINT questioningEvents found");
    }

    @Test
    @DisplayName("Should update existing VALINT event when one exists")
    void postValintQuestioningEventTest3() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent event = createQuestioningEvent(1L, TypeQuestioningEvent.VALINT, questioning, Clock.systemUTC());
        questioning.setQuestioningEvents(Set.of(event));
        questioningEventRepository.save(event);
        questioningRepository.save(questioning);
        QuestioningEventInputDto input = createValidedInputDto(questioningId);
        String valintEvent = TypeQuestioningEvent.VALINT.name();
        boolean result = questioningEventService.postQuestioningEvent(valintEvent, input);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should create new VALINT event when none exist")
    void postValintQuestioningEventTest4() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        questioningRepository.save(questioning);
        String valintEvent = TypeQuestioningEvent.VALINT.name();
        QuestioningEventInputDto input = createValidedInputDto(questioningId);
        boolean result = questioningEventService.postQuestioningEvent(valintEvent, input);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when no questioning event types match")
    void containsQuestioningEventsTest() {
        QuestioningEventDto dto = new QuestioningEventDto();
        dto.setType("VALINT");

        boolean result = questioningEventService.containsTypeQuestioningEvents(List.of(dto), List.of());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when questioning event matches given types")
    void containsQuestioningEventsTest2() {
        QuestioningEventDto dto = new QuestioningEventDto();
        dto.setType("VALINT");

        boolean result = questioningEventService.containsTypeQuestioningEvents(List.of(dto), TypeQuestioningEvent.VALIDATED_EVENTS);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when questioning events do not match given types")
    void containsQuestioningEventsTest3() {
        QuestioningEventDto dto = new QuestioningEventDto();
        dto.setType("VALINT");

        QuestioningEventDto dto2 = new QuestioningEventDto();
        dto2.setType("REFUSAL");

        boolean result = questioningEventService.containsTypeQuestioningEvents(List.of(dto,dto2), TypeQuestioningEvent.OPENED_EVENTS);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when no questioning events")
    void containsQuestioningEventsTest4() {

        boolean result = questioningEventService.containsTypeQuestioningEvents(List.of(), TypeQuestioningEvent.OPENED_EVENTS);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("postExpertEvent must throw NotFoundException when the questioning cannot be found")
    void postExpertEvent_questioningNotFound() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> questioningEventService.postExpertEvent(id, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Questioning "+ id +" not found");
    }

    @Test
    @DisplayName("postExpertEvent must create an EXPERT event if none already exists")
    void postExpertEvent_createExpert() {
        Questioning questioning = createQuestioning();
        questioningRepository.save(questioning);
        UUID questioningId = questioning.getId();
        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.EXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        Questioning updated = questioningRepository.findById(questioningId).orElseThrow();

        assertThat(updated.getScore()).isEqualTo(5);
        assertThat(updated.getScoreInit()).isEqualTo(5);

        List<QuestioningEvent> events = questioningEventRepository.findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.EXPERT);
        assertThat(events).hasSize(1);
    }

    @Test
    @DisplayName("postExpertEvent must create an VALID event if none already exists")
    void postExpertEvent_createVALID() {
        Questioning questioning = createQuestioning();
        questioningRepository.save(questioning);
        UUID questioningId = questioning.getId();

        ExpertEventDto dto = new ExpertEventDto(9, 9, TypeQuestioningEvent.VALID);

        questioningEventService.postExpertEvent(questioningId, dto);

        Questioning updated = questioningRepository.findById(questioningId).orElseThrow();

        assertThat(updated.getScore()).isEqualTo(9);
        assertThat(updated.getScoreInit()).isEqualTo(9);

        List<QuestioningEvent> events = questioningEventRepository.findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.VALID);
        assertThat(events).hasSize(1);
    }

    @Test
    @DisplayName("postExpertEvent must not duplicate the EXPERT event if an identical event already exists")
    void postExpertEvent_noDuplicateExpert() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent existing = createQuestioningEvent(1L, TypeQuestioningEvent.EXPERT, questioning);
        QuestioningEvent existing2 = createQuestioningEvent(1L, TypeQuestioningEvent.ONGEXPERT, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        questioning.setQuestioningEvents(Set.of(existing, existing2));
        questioningRepository.save(questioning);
        questioningEventRepository.save(existing);
        questioningEventRepository.save(existing2);

        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.EXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        List<QuestioningEvent> events = questioningEventRepository.findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.EXPERT);
        assertThat(events).hasSize(1);
    }

    @Test
    @DisplayName("postExpertEvent must create a ONGEXPERT event if the last one was EXPERT")
    void postExpertEvent_createONGEXPERTAfterExpert() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent existing = createQuestioningEvent(1L, TypeQuestioningEvent.EXPERT, questioning);
        questioning.setQuestioningEvents(Set.of(existing));
        questioningRepository.save(questioning);
        questioningEventRepository.save(existing);

        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.ONGEXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.ONGEXPERT);
        assertThat(events).hasSize(1);
    }

    @Test
    @DisplayName("postExpertEvent must not create a ONGEXPERT event if the last one was ONGEXPERT")
    void postExpertEvent_notcreateONGEXPERTAfterONGEXPERT() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent existing = createQuestioningEvent(1L, TypeQuestioningEvent.EXPERT, questioning);
        QuestioningEvent existing2 = createQuestioningEvent(2L, TypeQuestioningEvent.ONGEXPERT, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        questioning.setQuestioningEvents(Set.of(existing, existing2));
        questioningRepository.save(questioning);
        questioningEventRepository.save(existing);
        questioningEventRepository.save(existing2);

        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.ONGEXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.ONGEXPERT);
        assertThat(events).hasSize(1);
    }

    @Test
    @DisplayName("postExpertEvent must create a ONGEXPERT event if ONGEXPERT exist but the last one was VALID")
    void postExpertEvent_createONGEXPERTAfterVALID() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent existing = createQuestioningEvent(1L, TypeQuestioningEvent.EXPERT, questioning);
        QuestioningEvent existing2 = createQuestioningEvent(2L, TypeQuestioningEvent.ONGEXPERT, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        QuestioningEvent existing3 = createQuestioningEvent(3L, TypeQuestioningEvent.VALID, questioning,Clock.offset(Clock.systemUTC(), Duration.ofHours(2)));
        questioning.setQuestioningEvents(Set.of(existing, existing2, existing3));
        questioningRepository.save(questioning);
        questioningEventRepository.save(existing);
        questioningEventRepository.save(existing2);
        questioningEventRepository.save(existing3);

        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.ONGEXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.ONGEXPERT);
        assertThat(events).hasSize(2);
    }

    @Test
    @DisplayName("postExpertEvent must not create a ONGEXPERT event if the last one is null")
    void postExpertEvent_noExpertNoEvent() {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        questioningRepository.save(questioning);
        ExpertEventDto dto = new ExpertEventDto(5, 5, TypeQuestioningEvent.ONGEXPERT);

        questioningEventService.postExpertEvent(questioningId, dto);

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, TypeQuestioningEvent.ONGEXPERT);
        assertThat(events).isEmpty();
    }

}
