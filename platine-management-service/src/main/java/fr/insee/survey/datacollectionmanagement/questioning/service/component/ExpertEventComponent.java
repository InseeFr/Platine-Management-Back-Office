package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ExpertEventComponent {

    public QuestioningEvent getLastExpertEvent(Questioning questioning) {
        Set<QuestioningEvent> events = Optional.ofNullable(questioning.getQuestioningEvents())
                .orElse(Collections.emptySet());

        Optional<QuestioningEvent> lastEvent = events.stream()
                .filter(qe -> TypeQuestioningEvent.EXPERT_EVENTS.contains(qe.getType()))
                .max(Comparator.comparing(QuestioningEvent::getDate));

        if (lastEvent.isPresent()) {
            QuestioningEvent event = lastEvent.get();
            log.info("Last expert event is {}",  event.getType());
            return event;
        }

        return null;
    }

    public boolean isInitialExpertEventAllowed(TypeQuestioningEvent type) {
        boolean isInitialExpertEvent = EnumSet.of(
                TypeQuestioningEvent.EXPERT,
                TypeQuestioningEvent.VALID,
                TypeQuestioningEvent.NOQUAL
            ).contains(type);
        if (isInitialExpertEvent) {
            log.info("{} is an initial expert event",  type);
        }
        return isInitialExpertEvent;
    }

    public boolean isTransitionAllowed(TypeQuestioningEvent from, TypeQuestioningEvent to) {
        if (to == from) {
            log.info("The last event {} and the new event {} are identical: only scores are saved", from, to);
            return false;
        }

        boolean isTransitionAllowed = switch (to) {
            case NOQUAL  -> from != TypeQuestioningEvent.ENDEXPERT;
            case EXPERT  -> from == TypeQuestioningEvent.NOQUAL || from == TypeQuestioningEvent.VALID;
            default      -> true;
        };
        log.info("Transition allowed for {} and {}: {}", from, to,  isTransitionAllowed);
        return isTransitionAllowed;
    }
}
