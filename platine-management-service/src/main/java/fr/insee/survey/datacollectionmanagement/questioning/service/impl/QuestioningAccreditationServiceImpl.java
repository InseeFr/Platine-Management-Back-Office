package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestioningAccreditationServiceImpl implements QuestioningAccreditationService {

    private final QuestioningAccreditationRepository questioningAccreditationRepository;
    private final ContactEventService contactEventService;
    private final ContactService contactService;
    private final ContactSourceService contactSourceService;
    private final PartitioningService partitioningService;

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
    public QuestioningAccreditation findByQuestioningIdAndIsMain(Long questioningId) {
        return questioningAccreditationRepository.findAccreditationsByQuestioningIdAndIsMainTrue(questioningId).orElseThrow(() -> new NotFoundException(String.format("QuestioningAccreditation for %s questioningId not found", questioningId)));
    }

    @Override
    public void deleteAccreditation(QuestioningAccreditation acc) {
        questioningAccreditationRepository.deleteById(acc.getId());
    }

    @Override
    public void createQuestioningAccreditation(Questioning questioning, boolean isMain, String contactId, Date date)
    {
        QuestioningAccreditation questioningAccreditation = new QuestioningAccreditation();
        questioningAccreditation.setQuestioning(questioning);
        questioningAccreditation.setMain(isMain);
        questioningAccreditation.setIdContact(contactId);
        questioningAccreditation.setCreationDate(date);
        // questioningAccreditation.setCreationAuthor("platine-pilotage"); ?
        questioningAccreditationRepository.save(questioningAccreditation);
    }

    @Override
    public void setMainQuestioningAccreditationToContact(Contact contact, Questioning questioning) {
        Date date = Date.from(Instant.now());
        Source source = partitioningService.findById(questioning.getIdPartitioning())
                .getCampaign().getSurvey().getSource();
        JsonNode payload = createPayload("platine-pilotage");

        try {
            updateExistingMainAccreditationToNewContact(contact, questioning, payload, source);
        } catch (NotFoundException e) {
            createQuestioningAccreditation(questioning, true, contact.getIdentifier(), date);
        }

        logContactAccreditationGainUpdate(contact, questioning, payload, source);
    }

    @Override
    public void updateExistingMainAccreditationToNewContact(Contact newContact, Questioning questioning, JsonNode payload, Source source)  {
        QuestioningAccreditation existingAccreditation = findByQuestioningIdAndIsMain(questioning.getId());
        Contact previousContact = contactService.findByIdentifier(existingAccreditation.getIdContact());

        existingAccreditation.setIdContact(newContact.getIdentifier());
        saveQuestioningAccreditation(existingAccreditation);
        logContactAccrediationLossUpdate(previousContact, questioning, payload, source);
    }

    @Override
    public void logContactAccrediationLossUpdate(Contact contact, Questioning questioning, JsonNode payload, Source source) {
        contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, payload);
        contactSourceService.deleteContactSource(contact.getIdentifier(), source.getId(), questioning.getSurveyUnit().getIdSu());
    }

    @Override
    public void logContactAccreditationGainUpdate(Contact contact, Questioning questioning, JsonNode payload, Source source) {
        contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, payload);
        contactSourceService.saveContactSource(contact.getIdentifier(), source.getId(), questioning.getSurveyUnit().getIdSu(), true);
    }

    @Override
    public JsonNode createPayload(String sourceLabel) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("source", sourceLabel);
        return node;
    }
}
