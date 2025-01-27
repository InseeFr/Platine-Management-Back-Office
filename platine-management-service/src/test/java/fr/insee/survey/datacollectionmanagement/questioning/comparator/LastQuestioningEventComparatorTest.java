package fr.insee.survey.datacollectionmanagement.questioning.comparator;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.EventOrderServiceStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LastQuestioningEventComparatorTest {

    @Test
    @DisplayName("should sort list of question events")
    void sort_list_of_question_events() {

        // GIVEN
        EventOrderService eventOrderServiceStub = new EventOrderServiceStub();
        LastQuestioningEventComparator comparator = new LastQuestioningEventComparator(eventOrderServiceStub);
        comparator.afterPropertiesSet();
        List<QuestioningEvent> events = List.of(
                buildQuestioningEvent(1L, TypeQuestioningEvent.REFUSAL),
                buildQuestioningEvent(2L, TypeQuestioningEvent.VALINT),
                buildQuestioningEvent(3L, TypeQuestioningEvent.PARTIELINT),
                buildQuestioningEvent(4L, TypeQuestioningEvent.PND),
                buildQuestioningEvent(5L, TypeQuestioningEvent.HC)
        );

        // WHEN
        List<QuestioningEvent> result = events.stream()
                .sorted(comparator)
                .toList();

        // THEN
        assertThat(result).hasSize(5);
        List<Long> ids = result.stream().map(QuestioningEvent::getId).toList();
        assertThat(ids).containsExactly(
                1L, 2L, 5L, 3L, 4L
        );
    }

    private static QuestioningEvent buildQuestioningEvent(Long id, TypeQuestioningEvent type) {
        QuestioningEvent event = new QuestioningEvent();
        event.setId(id);
        event.setType(type);
        event.setDate(new Date());
        return event;
    }
}