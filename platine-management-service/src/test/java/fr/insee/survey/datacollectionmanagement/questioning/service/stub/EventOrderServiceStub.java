package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;

import java.util.List;

public class EventOrderServiceStub implements EventOrderService {

    @Override
    public EventOrder findByStatus(String status) {
        return null;
    }

    @Override
    public List<EventOrder> findAll() {
        return List.of(
                new EventOrder(1L, TypeQuestioningEvent.INITLA.name(), 1),
                new EventOrder(2L, TypeQuestioningEvent.PND.name(), 2),
                new EventOrder(3L, TypeQuestioningEvent.WASTE.name(), 3),
                new EventOrder(4L, TypeQuestioningEvent.PARTIELINT.name(), 4),
                new EventOrder(5L, TypeQuestioningEvent.HC.name(), 5),
                new EventOrder(6L, TypeQuestioningEvent.RECUPAP.name(), 6),
                new EventOrder(7L, TypeQuestioningEvent.VALINT.name(), 7),
                new EventOrder(8L, TypeQuestioningEvent.REFUSAL.name(), 8)
        );
    }
}
