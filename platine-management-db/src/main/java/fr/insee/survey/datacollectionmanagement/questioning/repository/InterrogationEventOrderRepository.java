package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.InterrogationEventOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterrogationEventOrderRepository extends JpaRepository<InterrogationEventOrder, Long> {
}
