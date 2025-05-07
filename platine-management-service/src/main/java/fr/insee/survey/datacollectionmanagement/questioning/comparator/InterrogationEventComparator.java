package fr.insee.survey.datacollectionmanagement.questioning.comparator;

import fr.insee.survey.datacollectionmanagement.questioning.domain.InterrogationEventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.InterrogationEventOrderRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InterrogationEventComparator implements Comparator<QuestioningEvent> {

    private final Map<String, Integer> eventOrderCache;

    public InterrogationEventComparator(InterrogationEventOrderRepository repository) {
        this.eventOrderCache = repository.findAll().stream()
                .collect(Collectors.toUnmodifiableMap(
                        InterrogationEventOrder::getStatus,
                        InterrogationEventOrder::getEventOrder));
    }

    @Override
    public int compare(QuestioningEvent a, QuestioningEvent b) {
        return Comparator
                .comparing((QuestioningEvent e) -> eventOrderCache.getOrDefault(e.getType().name(), Integer.MAX_VALUE))
                .thenComparing(QuestioningEvent::getDate)
                .compare(a, b);
    }
}
