package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExpertEventComponentTest {

    private final ExpertEventComponent component = new ExpertEventComponent();

    private static Questioning questioningWithEvents(QuestioningEvent... events) {
        Questioning q = new Questioning();
        Set<QuestioningEvent> set = new HashSet<>();
        for (QuestioningEvent e : events) {
            e.setQuestioning(q);
            set.add(e);
        }
        q.setQuestioningEvents(set);
        return q;
    }

    private static QuestioningEvent event(TypeQuestioningEvent type, Clock clock) {
        QuestioningEvent e = new QuestioningEvent();
        e.setType(type);
        e.setDate(Date.from(Instant.now(clock)));
        return e;
    }

    private static Clock at() {
        return Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
    }

    private static Clock plusHours(Clock base, long hours) {
        return Clock.offset(base, java.time.Duration.ofHours(hours));
    }

    @Test
    void returnsNull_whenQuestioningEventsIsNull() {
        Questioning q = new Questioning();
        q.setQuestioningEvents(null);

        QuestioningEvent last = component.getLastExpertEvent(q);

        assertThat(last).isNull();
    }

    @Test
    void returnsNull_whenNoEvents() {
        Questioning q = new Questioning();
        q.setQuestioningEvents(new HashSet<>());

        QuestioningEvent last = component.getLastExpertEvent(q);

        assertThat(last).isNull();
    }

    @Test
    void ignoresNonExpertTypes_like_VALINT() {
        Clock t0 = at();
        Questioning q = questioningWithEvents(
                event(TypeQuestioningEvent.VALINT, t0)
        );

        assertThat(component.getLastExpertEvent(q)).isNull();
    }

    @Test
    void picksOnlyExpertEvents_and_returnsMostRecent() {
        Clock t0 = at();
        Clock t1 = plusHours(t0, 1);
        Clock t2 = plusHours(t0, 2);

        Questioning q = questioningWithEvents(
                event(TypeQuestioningEvent.VALINT, plusHours(t0, -1)),
                event(TypeQuestioningEvent.EXPERT, t0),
                event(TypeQuestioningEvent.ONGEXPERT, t1),
                event(TypeQuestioningEvent.VALID, t2)
        );

        QuestioningEvent last = component.getLastExpertEvent(q);
        assertThat(last).isNotNull();
        assertThat(last.getType()).isEqualTo(TypeQuestioningEvent.VALID);
        assertThat(last.getDate()).isEqualTo(Date.from(Instant.now(t2)));
    }

    @Test
    void isInitialExpertEventAllowed_allows_EXPERT_asExample() {
        assertThat(component.isInitialExpertEventAllowed(TypeQuestioningEvent.EXPERT)).isTrue();
    }

    @Test
    void isInitialExpertEventAllowed_refuses_ONGEXPERT_asExample() {
        assertThat(component.isInitialExpertEventAllowed(TypeQuestioningEvent.ONGEXPERT)).isFalse();
    }

    @Test
    void transition_sameType_isNotAllowed_example() {
        assertThat(component.isTransitionAllowed(TypeQuestioningEvent.VALID, TypeQuestioningEvent.VALID)).isFalse();
    }

    @Test
    void transition_EXPERT_to_VALID_isNotAllowed_example() {
        assertThat(component.isTransitionAllowed(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.VALID)).isFalse();
    }

    @Test
    void transition_toEXPERT_onlyFrom_NOQUAL_allowed_example() {
        assertThat(component.isTransitionAllowed(TypeQuestioningEvent.NOQUAL, TypeQuestioningEvent.EXPERT)).isTrue();
    }

    @Test
    void transition_toEXPERT_fromOtherThan_NOQUAL_isNotAllowed_example() {
        assertThat(component.isTransitionAllowed(TypeQuestioningEvent.VALID, TypeQuestioningEvent.EXPERT)).isFalse();
    }

    @Test
    void transition_otherwise_genericAllowed_example() {
        assertThat(component.isTransitionAllowed(TypeQuestioningEvent.EXPERT, TypeQuestioningEvent.ONGEXPERT)).isTrue();
    }

}