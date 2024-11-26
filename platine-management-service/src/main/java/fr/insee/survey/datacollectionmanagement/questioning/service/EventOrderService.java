package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;

import java.util.List;

public interface EventOrderService {

    EventOrder findByStatus(String status);

    List<EventOrder> findAll();
}
