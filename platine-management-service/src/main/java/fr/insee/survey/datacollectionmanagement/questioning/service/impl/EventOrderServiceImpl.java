package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.repository.EventOrderRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventOrderServiceImpl implements EventOrderService {

    private final EventOrderRepository eventOrderRepository;

    public EventOrder saveAndFlush(EventOrder order) {
        return eventOrderRepository.saveAndFlush(order);
    }

    @Override
    public EventOrder findByStatus(String status) {
        return eventOrderRepository.findByStatus(status);
    }

}
