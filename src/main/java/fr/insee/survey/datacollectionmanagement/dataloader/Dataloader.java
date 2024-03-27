package fr.insee.survey.datacollectionmanagement.dataloader;

import fr.insee.survey.datacollectionmanagement.questioning.domain.EventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.repository.EventOrderRepository;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("poc")
@RequiredArgsConstructor
public class Dataloader {

    private final EventOrderRepository orderRepository;


    @PostConstruct
    public void init() {

        initOrder();

    }

    private void initOrder() {

        Long nbExistingOrders = orderRepository.count();
        log.info("{} orders in database", nbExistingOrders);

        if (nbExistingOrders != 8) {
            // Creating table order
            log.info("loading eventorder data");
            orderRepository.deleteAll();
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("8"), TypeQuestioningEvent.REFUSAL.toString(), 8));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("7"), TypeQuestioningEvent.VALINT.toString(), 7));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("6"), TypeQuestioningEvent.VALPAP.toString(), 6));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("5"), TypeQuestioningEvent.HC.toString(), 5));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("4"), TypeQuestioningEvent.PARTIELINT.toString(), 4));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("3"), TypeQuestioningEvent.WASTE.toString(), 3));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("2"), TypeQuestioningEvent.PND.toString(), 2));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("1"), TypeQuestioningEvent.INITLA.toString(), 1));
        }
    }


}
