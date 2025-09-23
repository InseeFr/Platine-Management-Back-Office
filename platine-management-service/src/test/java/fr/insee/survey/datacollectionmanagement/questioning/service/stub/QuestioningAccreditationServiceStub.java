package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Setter
public class QuestioningAccreditationServiceStub implements QuestioningAccreditationService {

    private List<QuestioningAccreditation> questioningAccreditationList = new ArrayList<>();

    @Override
    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return List.of();
    }

    @Override
    public boolean hasAccreditation(UUID questioningId, String contactId) {
        Optional<QuestioningAccreditation> accreditation = questioningAccreditationList.stream().filter(
                q -> q.getQuestioning().getId().equals(questioningId)
                        && q.getIdContact().equals(contactId)).findFirst();
        return accreditation.isPresent();
    }

    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public QuestioningAccreditation findById(Long id) {
        return null;
    }

    @Override
    public QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation) {
        return null;
    }

    @Override
    public void deleteAccreditation(QuestioningAccreditation c) {
        //not used
    }

    @Override
    public void createQuestioningAccreditation(Questioning questioning, boolean isMain, Contact contact, JsonNode payload, Date date, Campaign campaign, Boolean isNew) {
        // not used
    }


    @Override
    public void setQuestioningAccreditationAsMain(QuestioningAccreditation qa, Contact contact, JsonNode eventPayload) {
        // not used
    }

  @Override
  public void setMainQuestioningAccreditationToContact(String contactId, UUID questioningId,
      Boolean isNew) {
      // not used
  }

    @Override
    public void updateExistingMainAccreditationToNewContact(QuestioningAccreditation existingAccreditation, Contact newContact, String surveyUnitId, JsonNode payload, Campaign campaign, Boolean isNew) {
        // not used
    }

    @Override
    public void logContactAccreditationLossUpdate(Contact contact, String surveyUnitId, JsonNode payload, Campaign campaign, Boolean isNew) {
        // not used
    }

    @Override
    public void logContactAccreditationGainUpdate(Contact contact, String surveyUnitId, JsonNode payload, Campaign campaign, Boolean isNew) {
        // not used
    }
}
