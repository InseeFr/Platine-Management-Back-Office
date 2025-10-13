package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.exception.ForbiddenAccessException;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.ExpertEventComponent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.InterrogationEventOrderRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class QuestioningEventServiceImplTest {

    private QuestioningEventRepositoryStub questioningEventRepository;

    private QuestioningRepositoryStub questioningRepository;

    private QuestioningEventServiceImpl questioningEventService;

    @BeforeEach
    void setUp() {
        questioningEventRepository = new QuestioningEventRepositoryStub();
        questioningRepository = new QuestioningRepositoryStub();
        InterrogationEventComparator interrogationEventComparator = new InterrogationEventComparator(new InterrogationEventOrderRepositoryStub());
        questioningEventService = new QuestioningEventServiceImpl(
                null,
                questioningEventRepository,
                questioningRepository,
                new ModelMapper(),
                interrogationEventComparator,
                new ExpertEventComponent());
    }

    private Questioning createQuestioning() {
        Questioning questioning = new Questioning();
        questioning.setId(UUID.randomUUID());
        questioningRepository.save(questioning);
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
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(event);
        questioningEvents.add(event2);
        questioning.setQuestioningEvents(questioningEvents);
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
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(event);
        questioning.setQuestioningEvents(questioningEvents);
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

    @ParameterizedTest(name = "OK | init={0} -> post={1}({2},{3})")
    @MethodSource("okCases")
    void postExpertEvent_OK(TypeQuestioningEvent initialType,
                            TypeQuestioningEvent postedType,
                            int score, int scoreInit) {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent init = createQuestioningEvent(1L, initialType, questioning);
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(init);
        questioning.setQuestioningEvents(questioningEvents);

        questioningRepository.save(questioning);
        questioningEventRepository.saveAll(questioning.getQuestioningEvents());

        questioningEventService.postExpertEvent(questioningId, new ExpertEventDto(score, scoreInit, postedType));

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, postedType);
        assertThat(events).hasSize(1);

        Questioning updated = questioningRepository.findById(questioningId).orElseThrow();
        assertThat(updated.getScore()).isEqualTo(score);
        assertThat(updated.getScoreInit()).isEqualTo(scoreInit);
    }

    private static Stream<Arguments> okCases() {
        return Stream.of(
                Arguments.of(TypeQuestioningEvent.VALINT, TypeQuestioningEvent.EXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.VALINT, TypeQuestioningEvent.VALID,  9, 9),
                Arguments.of(TypeQuestioningEvent.VALINT, TypeQuestioningEvent.NOQUAL, 0, 0),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ONGEXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.NOQUAL, 0, 0),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ENDEXPERT, 7, 7),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.VALID, 9, 9),
                Arguments.of(TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.VALID, 9, 9),
                Arguments.of(TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.NOQUAL, 0, 0),
                Arguments.of(TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.ENDEXPERT, 7, 7),
                Arguments.of(TypeQuestioningEvent.VALID, TypeQuestioningEvent.ONGEXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.VALID, TypeQuestioningEvent.NOQUAL, 0, 0),
                Arguments.of(TypeQuestioningEvent.VALID, TypeQuestioningEvent.ENDEXPERT, 7, 7),
                Arguments.of(TypeQuestioningEvent.VALID, TypeQuestioningEvent.EXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.VALID, 9, 9),
                Arguments.of(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.ONGEXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.EXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.ENDEXPERT, 7, 7),
                Arguments.of(TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.ONGEXPERT, 5, 5),
                Arguments.of(TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.VALID, 9, 9)
        );
    }

    @ParameterizedTest(name = "NOT OK | init={0} -> post={1}")
    @MethodSource("notOkCases")
    void postExpertEvent_NOT_OK(TypeQuestioningEvent initialType,
                            TypeQuestioningEvent postedType) {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent init = createQuestioningEvent(1L, initialType, questioning);
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(init);
        questioning.setQuestioningEvents(questioningEvents);

        questioningRepository.save(questioning);
        questioningEventRepository.saveAll(questioning.getQuestioningEvents());

        questioningEventService.postExpertEvent(questioningId, new ExpertEventDto(0, 0, postedType));

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, postedType);
        assertThat(events).isEmpty();
    }

    private static Stream<Arguments> notOkCases() {
        return Stream.of(
                Arguments.of(TypeQuestioningEvent.VALINT, TypeQuestioningEvent.ONGEXPERT),
                Arguments.of(TypeQuestioningEvent.VALINT, TypeQuestioningEvent.ENDEXPERT),
                Arguments.of(TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.EXPERT),
                Arguments.of(TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.EXPERT),
                Arguments.of(TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.NOQUAL)
        );
    }

    @ParameterizedTest(name = "NOT OK duplicate | init={0},{1} -> post={2}")
    @MethodSource("notOkCasesDuplicate")
    void postExpertEvent_NOT_OK_Duplicate(TypeQuestioningEvent initialType,
                                          TypeQuestioningEvent initialType2,
                                          TypeQuestioningEvent postedType) {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent init = createQuestioningEvent(1L, initialType, questioning);
        QuestioningEvent init2 = createQuestioningEvent(2L, initialType2, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(init);
        questioningEvents.add(init2);
        questioning.setQuestioningEvents(questioningEvents);

        questioningRepository.save(questioning);
        questioningEventRepository.saveAll(questioning.getQuestioningEvents());

        questioningEventService.postExpertEvent(questioningId, new ExpertEventDto(0, 0, postedType));

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, postedType);
        assertThat(events).hasSize(1);
    }

    private static Stream<Arguments> notOkCasesDuplicate() {
        return Stream.of(
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.EXPERT),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.ONGEXPERT),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.VALID, TypeQuestioningEvent.VALID),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.ENDEXPERT),
                Arguments.of(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.NOQUAL)
        );
    }

    @ParameterizedTest(name = "OK duplicate | init={0},{1} -> post={2}")
    @MethodSource("okCasesDuplicate")
    void postExpertEvent_OK_Duplicate(TypeQuestioningEvent initialType,
                                          TypeQuestioningEvent initialType2,
                                          TypeQuestioningEvent postedType) {
        Questioning questioning = createQuestioning();
        UUID questioningId = questioning.getId();
        QuestioningEvent init = createQuestioningEvent(1L, initialType, questioning);
        QuestioningEvent init2 = createQuestioningEvent(2L, initialType2, questioning, Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
        Set<QuestioningEvent> questioningEvents = new HashSet<>();
        questioningEvents.add(init);
        questioningEvents.add(init2);
        questioning.setQuestioningEvents(questioningEvents);

        questioningRepository.save(questioning);
        questioningEventRepository.saveAll(questioning.getQuestioningEvents());

        questioningEventService.postExpertEvent(questioningId, new ExpertEventDto(0, 0, postedType));

        List<QuestioningEvent> events = questioningEventRepository
                .findByQuestioningIdAndType(questioningId, postedType);
        assertThat(events).hasSize(2);
    }

    private static Stream<Arguments> okCasesDuplicate() {
        return Stream.of(
                Arguments.of(TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.ONGEXPERT),
                Arguments.of(TypeQuestioningEvent.VALID, TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.VALID),
                Arguments.of(TypeQuestioningEvent.ENDEXPERT, TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.ENDEXPERT),
                Arguments.of(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.ONGEXPERT, TypeQuestioningEvent.NOQUAL)
        );
    }

    @Test
    @DisplayName("findById should return exception if not exist")
    void findById_not_exist() {
        Long id = 1L;
        assertThatThrownBy(() -> questioningEventService.findbyId(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("QuestioningEvent %s not found", id));
    }

    @Test
    @DisplayName("should save questioning event and refresh highest event")
    void saveQuestioningEvent() {
        Questioning questioning = createQuestioning();
        questioningRepository.save(questioning);
        QuestioningEvent questioningEvent = createQuestioningEvent(1L, TypeQuestioningEvent.INITLA, questioning);
        QuestioningEvent saved = questioningEventService.saveQuestioningEvent(questioningEvent);
        assertThat(saved).isNotNull();
        assertThat(saved.getQuestioning()).isNotNull();
        assertThat(saved.getQuestioning().getHighestEventType()).isNotNull()
                .isEqualTo(TypeQuestioningEvent.INITLA);
    }

    @Test
    @DisplayName("delete refresh")
    void deleteWithRefreshHighestEvent() {
        Questioning questioning = createQuestioning();
        questioningRepository.save(questioning);

        QuestioningEvent event = createQuestioningEvent(2L, TypeQuestioningEvent.INITLA, questioning);
        QuestioningEvent event2 = createQuestioningEvent(3L, TypeQuestioningEvent.PARTIELINT, questioning);
        questioningEventRepository.save(event);
        questioningEventRepository.save(event2);

        questioning.setHighestEventDate(event2.getDate());
        questioning.setHighestEventType(event2.getType());
        questioningRepository.save(questioning);
        questioningEventService.deleteQuestioningEvent(3L);
        questioningRepository.flush();

        assertThat(questioning.getHighestEventType()).isEqualTo(event.getType());
        assertThat(questioning.getHighestEventDate()).isEqualTo(event.getDate());
    }

    @Test
    void shouldSetNullWhenNoEvents() {
        UUID id = UUID.randomUUID();
        Questioning questioning = new Questioning();
        questioning.setId(id);
        questioning.setQuestioningEvents(null);
        questioningRepository.save(questioning);

        questioningEventService.refreshHighestEvent(id);

        Questioning updated = questioningRepository.findById(id).orElseThrow();
        assertThat(updated.getHighestEventType()).as("HighestEventType should be null when no events").isNull();
        assertThat(updated.getHighestEventDate()).as("HighestEventDate should be null when no events").isNull();
    }

    @Test
    void shouldPickLatestInterrogationEvent() {
        UUID id = UUID.randomUUID();
        Questioning questioning = new Questioning();
        questioning.setId(id);

        QuestioningEvent evtInit = new QuestioningEvent();
        evtInit.setType(TypeQuestioningEvent.INITLA);
        Date dateInit = new GregorianCalendar(2025, Calendar.JANUARY, 10).getTime();
        evtInit.setDate(dateInit);

        QuestioningEvent evtPart = new QuestioningEvent();
        evtPart.setType(TypeQuestioningEvent.PARTIELINT);
        Date datePart = new GregorianCalendar(2025, Calendar.FEBRUARY, 20).getTime();
        evtPart.setDate(datePart);

        QuestioningEvent evtVal = new QuestioningEvent();
        evtVal.setType(TypeQuestioningEvent.VALINT);
        Date dateVal = new GregorianCalendar(2025, Calendar.MARCH, 5).getTime();
        evtVal.setDate(dateVal);

        questioning.setQuestioningEvents(Set.of(evtInit, evtPart, evtVal));
        questioningRepository.save(questioning);

        questioningEventService.refreshHighestEvent(id);

        Questioning updated = questioningRepository.findById(id).orElseThrow();
        assertThat(updated.getHighestEventType())
                .as("Should pick the event with highest order by comparator")
                .isEqualTo(TypeQuestioningEvent.VALINT);
        assertThat(updated.getHighestEventDate())
                .as("Should pick the correct date of the highest event")
                .isEqualTo(dateVal);
    }

    @Test
    void shouldThrowWhenQuestioningNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> questioningEventService.refreshHighestEvent(unknownId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }



    @Test
    @DisplayName("canUserDeleteQuestioningEvent shouldn't throw a forbidden exception and delete questioning event when user has given rights for deleting a specific event type")
    void canUserDeleteQuestioningEventTest1() {

        Questioning questioning = createQuestioning();

        for (TypeQuestioningEvent typeQuestioningEvent : TypeQuestioningEvent.values())
        {
            QuestioningEvent questioningEvent = createQuestioningEvent(1L, typeQuestioningEvent, questioning, Clock.systemUTC());
            questioningEventRepository.save(questioningEvent);
            assertThat(questioningEventRepository.findById(1L)).isPresent();
            assertThatNoException().isThrownBy(() -> questioningEventService.deleteQuestioningEventIfSpecificRole(List.of(AuthorityRoleEnum.ADMIN.securityRole()), 1L, typeQuestioningEvent));
            assertThat(questioningEventRepository.findById(1L)).isNotPresent();
        }

        for (TypeQuestioningEvent typeQuestioningEvent : TypeQuestioningEvent.REFUSED_EVENTS)
        {
            QuestioningEvent questioningEvent = createQuestioningEvent(1L, typeQuestioningEvent, questioning, Clock.systemUTC());
            questioningEventRepository.save(questioningEvent);
            assertThat(questioningEventRepository.findById(1L)).isPresent();
            assertThatNoException().isThrownBy(() -> questioningEventService.deleteQuestioningEventIfSpecificRole(List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole()), 1L, typeQuestioningEvent));
            assertThat(questioningEventRepository.findById(1L)).isNotPresent();
        }
    }

    @Test
    @DisplayName("canUserDeleteQuestioningEvent should throw forbidden exception and no delete questioning event when user doest not have given rights for deleting a specific event type")
    void canUserDeleteQuestioningEventTest2() {

        Questioning questioning = createQuestioning();
        List<String> managementExcludedRoles = AuthorityRoleEnum.MANAGEMENT_EXCLUDED_SECURITY_ROLES;

        for (TypeQuestioningEvent typeQuestioningEvent : TypeQuestioningEvent.values())
        {
            long id = new Random().nextLong();
            QuestioningEvent questioningEvent = createQuestioningEvent(id, typeQuestioningEvent, questioning, Clock.systemUTC());
            questioningEventRepository.save(questioningEvent);
            assertThat(questioningEventRepository.findById(id)).isPresent();
            assertThatThrownBy(() ->  questioningEventService.deleteQuestioningEventIfSpecificRole(managementExcludedRoles,  id, typeQuestioningEvent))
                    .isInstanceOf(ForbiddenAccessException.class)
                    .hasMessage(String.format("User role %s is not allowed to delete questioning event of type %s", managementExcludedRoles, typeQuestioningEvent));
            assertThat(questioningEventRepository.findById(id)).isPresent();
        }

        List<TypeQuestioningEvent> typeQuestioningEventsWithoutRefused = Arrays.stream(TypeQuestioningEvent.values())
                .filter(p -> !TypeQuestioningEvent.REFUSED_EVENTS.contains(p))
                .toList();

        for (TypeQuestioningEvent typeQuestioningEvent : typeQuestioningEventsWithoutRefused)
        {
            long id = new Random().nextLong();
            List<String> userRoles =  List.of(AuthorityRoleEnum.INTERNAL_USER.securityRole());
            QuestioningEvent questioningEvent = createQuestioningEvent(id, typeQuestioningEvent, questioning, Clock.systemUTC());
            questioningEventRepository.save(questioningEvent);
            assertThat(questioningEventRepository.findById(id)).isPresent();
            assertThatThrownBy(() ->  questioningEventService.deleteQuestioningEventIfSpecificRole(userRoles, id, typeQuestioningEvent))
                    .isInstanceOf(ForbiddenAccessException.class)
                    .hasMessage(String.format("User role %s is not allowed to delete questioning event of type %s", userRoles, typeQuestioningEvent));
            assertThat(questioningEventRepository.findById(id)).isPresent();
        }
    }

}
