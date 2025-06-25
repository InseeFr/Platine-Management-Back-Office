package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestioningEventRepository extends JpaRepository<QuestioningEvent, Long> {
    
    List<QuestioningEvent> findByQuestioningIdAndType(Long questioningId, TypeQuestioningEvent type);

    Long countByUploadId(Long idupload);

    List<QuestioningEvent> findByQuestioningId(Long questioningId);
}
