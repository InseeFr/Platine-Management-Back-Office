package fr.insee.survey.datacollectionmanagement.questioning.util;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LastQuestioningEventComparator implements Comparator<QuestioningEvent>, InitializingBean {

    private final EventOrderService eventOrderService;
    private Map<String, Integer> eventOrderCache;

    @Override
    public void afterPropertiesSet() {
        eventOrderCache = eventOrderService.findAll().stream()
                .collect(Collectors.toMap(EventOrder::getStatus, EventOrder::getEventOrder));
    }

    @Override
    public int compare(QuestioningEvent o1, QuestioningEvent o2) {
        Integer eventOrder1 = eventOrderCache.get(o1.getType().name());
        Integer eventOrder2 = eventOrderCache.get(o2.getType().name());

        return Integer.compare(eventOrder2, eventOrder1);
    }
}
