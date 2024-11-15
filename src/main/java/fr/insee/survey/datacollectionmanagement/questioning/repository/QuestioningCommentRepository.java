package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestioningCommentRepository extends JpaRepository<QuestioningComment, Long> {

}
