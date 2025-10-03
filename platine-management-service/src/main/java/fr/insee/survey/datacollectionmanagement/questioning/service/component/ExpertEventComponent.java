package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExpertEventComponent {

    public QuestioningEvent getLastExpertEvent(Questioning questioning) {
        Set<QuestioningEvent> events = Optional.ofNullable(questioning.getQuestioningEvents())
                .orElse(Collections.emptySet());

        return events.stream()
                .filter(qe -> TypeQuestioningEvent.EXPERT_EVENTS.contains(qe.getType()))
                .max(Comparator.comparing(QuestioningEvent::getDate))
                .orElse(null);
    }

    public boolean isInitialExpertEventAllowed(TypeQuestioningEvent type) {
        return EnumSet.of(
                TypeQuestioningEvent.EXPERT,
                TypeQuestioningEvent.VALID,
                TypeQuestioningEvent.NOQUAL
        ).contains(type);
    }

    public boolean isTransitionAllowed(TypeQuestioningEvent from, TypeQuestioningEvent to) {
        if (to == from) return false;
        if (from == TypeQuestioningEvent.EXPERT && to == TypeQuestioningEvent.VALID) return false;
        return to != TypeQuestioningEvent.EXPERT || from == TypeQuestioningEvent.NOQUAL;
    }
}
