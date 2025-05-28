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

import java.util.Date;
import java.util.List;

@Setter
public class QuestioningAccreditationServiceStub implements QuestioningAccreditationService {

    private List<QuestioningAccreditation> questioningAccreditationList;

    @Override
    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationList;
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
    public void createQuestioningAccreditation (Questioning questioning,
                                                boolean isMain,
                                                Contact contact,
                                                JsonNode payload,
                                                Date date,
                                                Campaign campaign)
    {
        // not used
    }

    @Override
    public void setMainQuestioningAccreditationToContact(Contact contact, Questioning questioning) {
        // not used
    }

    @Override
    public void updateExistingMainAccreditationToNewContact(QuestioningAccreditation existingAccreditation, Contact newContact, Questioning questioning, JsonNode payload, Campaign campaign) {
        // not used
    }

    @Override
    public void logContactAccreditationLossUpdate(Contact contact, Questioning questioning, JsonNode payload, Campaign campaign) {
        // not used
    }

    @Override
    public void logContactAccreditationGainUpdate(Contact contact, Questioning questioning, JsonNode payload, Campaign campaign) {
        // not used
    }

    @Override
    public JsonNode createPayload(String sourceLabel) {
        return null;
    }
}
