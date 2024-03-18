package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestioningAccreditationService {

    List<QuestioningAccreditation> findByContactIdentifier(String id);


    Page<QuestioningAccreditation> findAll(Pageable pageable);

    QuestioningAccreditation findById(Long id);
   
    QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation);

    void deleteAccreditation(QuestioningAccreditation c);

}
