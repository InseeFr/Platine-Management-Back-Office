package fr.insee.survey.datacollectionmanagement.questioning.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface QuestioningAccreditationService {

    List<QuestioningAccreditation> findByContactIdentifier(String id);

    Page<QuestioningAccreditation> findAll(Pageable pageable);

    QuestioningAccreditation findById(Long id);
   
    QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation);

    void deleteAccreditation(QuestioningAccreditation c);

    void createQuestioningAccreditation(Questioning questioning,
                                        boolean isMain,
                                        Contact contact,
                                        JsonNode payload,
                                        Date date,
                                        Campaign campaign);

    void setQuestioningAccreditationAsMain(QuestioningAccreditation qa, Contact contact, JsonNode eventPayload);

    void setMainQuestioningAccreditationToContact(String contactId, UUID questioningId);

    void updateExistingMainAccreditationToNewContact(QuestioningAccreditation existingAccreditation,
                                                     Contact newContact,
                                                     String surveyUnitId,
                                                     JsonNode payload,
                                                     Campaign campaign);

    void logContactAccreditationLossUpdate(Contact contact,
                                           String surveyUnitId,
                                           JsonNode payload,
                                           Campaign campaign);

    void logContactAccreditationGainUpdate(Contact contact,
                                           String surveyUnitId,
                                           JsonNode payload,
                                           Campaign campaign);
}
