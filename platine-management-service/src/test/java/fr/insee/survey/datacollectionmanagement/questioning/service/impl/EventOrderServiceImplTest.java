package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.repository.EventOrderRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.EventOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOrderServiceImplTest {

    private EventOrderService service;

    @Mock
    private EventOrderRepository eventOrderRepository;

    @BeforeEach
    void setUp() {
         service = new EventOrderServiceImpl(eventOrderRepository);
    }

    @Test
    @DisplayName("Should find event order by status")
    void findByStatus() {
        // GIVEN
        String status = "status";
        when(eventOrderRepository.findByStatus(status)).thenReturn(new EventOrder());
        // WHEN
        EventOrder result = service.findByStatus(status);
        // THEN
        assertThat(result).isNotNull();
        verify(eventOrderRepository).findByStatus(anyString());
    }

    @Test
    @DisplayName("Should find all event orders")
    void findAll() {
        // GIVEN
        when(eventOrderRepository.findAll()).thenReturn(List.of(new EventOrder(), new EventOrder()));
        // WHEN
        List<EventOrder> result = service.findAll();
        // THEN
        assertThat(result).isNotNull().hasSize(2);
        verify(eventOrderRepository).findAll();
    }
}