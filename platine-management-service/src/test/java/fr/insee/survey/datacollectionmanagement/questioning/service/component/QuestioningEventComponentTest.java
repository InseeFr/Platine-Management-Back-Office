package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.comparator.InterrogationEventComparator;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.InterrogationEventOrderRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class QuestioningEventComponentTest {

    private QuestioningRepositoryStub questioningRepository;
    private QuestioningEventComponent component;

    @BeforeEach
    void setUp() {
        questioningRepository = new QuestioningRepositoryStub();
        component = new QuestioningEventComponent(
                questioningRepository,
                new InterrogationEventComparator(new InterrogationEventOrderRepositoryStub())
        );
    }

    @Test
    void shouldSetNullWhenNoEvents() {
        UUID id = UUID.randomUUID();
        Questioning questioning = new Questioning();
        questioning.setId(id);
        questioning.setQuestioningEvents(null);
        questioningRepository.save(questioning);

        component.refreshHighestEvent(id);

        Questioning updated = questioningRepository.findById(id).orElseThrow();
        assertThat(updated.getHighestTypeEvent()).as("HighestTypeEvent should be null when no events").isNull();
        assertThat(updated.getHighestDateEvent()).as("HighestDateEvent should be null when no events").isNull();
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

        component.refreshHighestEvent(id);

        Questioning updated = questioningRepository.findById(id).orElseThrow();
        assertThat(updated.getHighestTypeEvent())
                .as("Should pick the event with highest order by comparator")
                .isEqualTo(TypeQuestioningEvent.VALINT);
        assertThat(updated.getHighestDateEvent())
                .as("Should pick the correct date of the highest event")
                .isEqualTo(dateVal);
    }

    @Test
    void shouldThrowWhenQuestioningNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> component.refreshHighestEvent(unknownId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }


}