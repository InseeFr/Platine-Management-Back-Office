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
                new EventOrder(1L, TypeQuestioningEvent.INITLA.toString(), 1),
                new EventOrder(2L, TypeQuestioningEvent.PND.toString(), 2),
                new EventOrder(3L, TypeQuestioningEvent.WASTE.toString(), 3),
                new EventOrder(4L, TypeQuestioningEvent.PARTIELINT.toString(), 4),
                new EventOrder(5L, TypeQuestioningEvent.HC.toString(), 5),
                new EventOrder(6L, TypeQuestioningEvent.VALPAP.toString(), 6),
                new EventOrder(7L, TypeQuestioningEvent.VALINT.toString(), 7),
                new EventOrder(8L, TypeQuestioningEvent.REFUSAL.toString(), 8)
        );
    }
}
