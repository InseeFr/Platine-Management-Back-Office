package fr.insee.survey.datacollectionmanagement.questioning.repository;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestioningAccreditationRepository extends JpaRepository<QuestioningAccreditation, Long> {

    List<QuestioningAccreditation> findByIdContact(String idContact);

}
