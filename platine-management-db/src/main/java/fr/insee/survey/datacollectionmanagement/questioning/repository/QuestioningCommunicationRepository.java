package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestioningCommunicationRepository extends JpaRepository<QuestioningCommunication, Long> {

  List<QuestioningCommunication> findByQuestioningId(UUID questioningId);


}
