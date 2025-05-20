package fr.insee.survey.datacollectionmanagement.questioning.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface QuestioningAccreditationService {

    List<QuestioningAccreditation> findByContactIdentifier(String id);

    Page<QuestioningAccreditation> findAll(Pageable pageable);

    QuestioningAccreditation findById(Long id);
   
    QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation);

    QuestioningAccreditation findByQuestioningIdAndIsMain(Long questioningId);

    void deleteAccreditation(QuestioningAccreditation c);

    void createQuestioningAccreditation(Questioning questioning, boolean isMain, String contactId, Date date);

    void setMainQuestioningAccreditationToContact(Contact contact, Questioning questioning);

    void updateExistingAccreditation(Contact contact, Questioning questioning, JsonNode payload, Source source) throws NotFoundException;

    void logContactUpdate(Contact contact, Questioning questioning, JsonNode payload, Source source);

    JsonNode createPayload(String sourceLabel);
}
