package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestioningEventRepository extends JpaRepository<QuestioningEvent, Long> {
    
    List<QuestioningEvent> findByQuestioningId(Long questioningId);

    Long countByUploadId(Long idupload);
}
