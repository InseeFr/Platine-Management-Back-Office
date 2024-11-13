package fr.insee.survey.datacollectionmanagement.questioning.comparator;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class LastQuestioningEventComparator implements Comparator<QuestioningEvent> {

    private final EventOrderService eventOrderService;

    @Override
    public int compare(QuestioningEvent o1, QuestioningEvent o2) {
        EventOrder eventOrder1 = eventOrderService.findByStatus(o1.getType().name());
        EventOrder eventOrder2 = eventOrderService.findByStatus(o2.getType().name());

        return Integer.compare(eventOrder2.getEventOrder(), eventOrder1.getEventOrder());
    }

}
