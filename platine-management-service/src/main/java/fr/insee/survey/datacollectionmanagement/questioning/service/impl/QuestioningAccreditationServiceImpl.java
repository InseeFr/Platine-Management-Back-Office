package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestioningAccreditationServiceImpl implements QuestioningAccreditationService {

    private final QuestioningAccreditationRepository questioningAccreditationRepository;
    private final ContactEventService contactEventService;
    private final ContactService contactService;
    private final ContactSourceService contactSourceService;
    private final PartitioningService partitioningService;
    private final ViewService viewService;

    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationRepository.findByIdContact(id);
    }

    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return questioningAccreditationRepository.findAll(pageable);
    }

    @Override
    public QuestioningAccreditation findById(Long id) {
        return questioningAccreditationRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("QuestioningAccreditation %s not found", id)));
    }

    @Override
    public QuestioningAccreditation saveQuestioningAccreditation(QuestioningAccreditation questioningAccreditation) {
        return questioningAccreditationRepository.save(questioningAccreditation);
    }

    @Override
    public void deleteAccreditation(QuestioningAccreditation acc) {
        questioningAccreditationRepository.deleteById(acc.getId());
    }

    @Override
    public void createQuestioningAccreditation(Questioning questioning,
                                               boolean isMain,
                                               Contact contact,
                                               JsonNode payload,
                                               Date date,
                                               Campaign campaign)
    {
        QuestioningAccreditation questioningAccreditation = new QuestioningAccreditation();
        questioningAccreditation.setQuestioning(questioning);
        questioningAccreditation.setMain(isMain);
        questioningAccreditation.setIdContact(contact.getIdentifier());
        questioningAccreditation.setCreationDate(date);
        // questioningAccreditation.setCreationAuthor("platine-pilotage"); ?
        questioningAccreditationRepository.save(questioningAccreditation);

        logContactAccreditationGainUpdate(contact, questioning, payload, campaign);
    }

    @Override
    public void setMainQuestioningAccreditationToContact(Contact contact, Questioning questioning) {
        Date date = Date.from(Instant.now());
        Campaign campaign = partitioningService.findById(questioning.getIdPartitioning()).getCampaign();
        JsonNode payload = createPayload("platine-pilotage");

        Optional<QuestioningAccreditation> questioningAccreditation = questioningAccreditationRepository
        .findAccreditationsByQuestioningIdAndIsMainTrue(questioning.getId());

        questioningAccreditation.ifPresentOrElse(
                accreditation -> updateExistingMainAccreditationToNewContact(accreditation, contact, questioning, payload, campaign),
                () -> createQuestioningAccreditation(questioning, true, contact, payload, date, campaign));
    }

    @Override
    public void updateExistingMainAccreditationToNewContact(QuestioningAccreditation existingAccreditation,
                                                            Contact newContact,
                                                            Questioning questioning,
                                                            JsonNode payload,
                                                            Campaign campaign)  {

        if(existingAccreditation.getIdContact().equals(newContact.getIdentifier()))
        {
            return;
        }

        Contact previousContact = contactService.findByIdentifier(existingAccreditation.getIdContact());
        existingAccreditation.setIdContact(newContact.getIdentifier());
        saveQuestioningAccreditation(existingAccreditation);
        logContactAccreditationLossUpdate(previousContact, questioning, payload, campaign);
        logContactAccreditationGainUpdate(newContact, questioning, payload, campaign);

    }

    @Override
    public void logContactAccreditationLossUpdate(Contact contact,
                                                  Questioning questioning,
                                                  JsonNode payload,
                                                  Campaign campaign) {


            Optional<View> viewToDelete = viewService.findByIdentifierAndIdSuAndCampaignId(
                    contact.getIdentifier(),
                    questioning.getSurveyUnit().getIdSu(),
                    campaign.getId());

            viewToDelete.ifPresent(viewService::deleteView);

            List<View> views = viewService.findViewByIdentifier(contact.getIdentifier());

            if(views.isEmpty())
            {
                View defaulView = new View();
                defaulView.setIdentifier(contact.getIdentifier());
                viewService.saveView(defaulView);
            }

        ContactEvent contactEvent = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, payload);
        contactEventService.saveContactEvent(contactEvent);

        contactSourceService.deleteContactSource(
                contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                questioning.getSurveyUnit().getIdSu());
    }

    @Override
    public void logContactAccreditationGainUpdate(Contact contact,
                                                  Questioning questioning,
                                                  JsonNode payload,
                                                  Campaign campaign) {

        ContactEvent contactEvent = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, payload);
        contactEventService.saveContactEvent(contactEvent);

        contactSourceService.saveContactSource(
                contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                questioning.getSurveyUnit().getIdSu(),
                true);

        viewService.createViewAndDeleteEmptyExistingOnesByIdentifier(contact.getIdentifier(), questioning.getSurveyUnit().getIdSu(), campaign.getId());
    }

    @Override
    public JsonNode createPayload(String sourceLabel) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("source", sourceLabel);
        return node;
    }
}
