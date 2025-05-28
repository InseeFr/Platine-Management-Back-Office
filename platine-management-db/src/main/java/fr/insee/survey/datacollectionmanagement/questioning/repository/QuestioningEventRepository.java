package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestioningEventRepository extends JpaRepository<QuestioningEvent, Long> {
    
    List<QuestioningEvent> findByQuestioningIdAndType(UUID questioningId, TypeQuestioningEvent type);

    Long countByUploadId(Long idupload);

    List<QuestioningEvent> findByQuestioningId(Long questioningId);
}
