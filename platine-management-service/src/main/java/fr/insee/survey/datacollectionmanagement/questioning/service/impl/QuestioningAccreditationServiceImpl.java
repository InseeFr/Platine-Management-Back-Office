package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.AccreditationContext;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.util.ServiceJsonUtil;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestioningAccreditationServiceImpl implements QuestioningAccreditationService {

    private final QuestioningAccreditationRepository questioningAccreditationRepository;
    private final ContactEventService contactEventService;
    private final ContactSourceService contactSourceService;
    private final PartitioningService partitioningService;
    private final ViewService viewService;
    private final QuestioningRepository questioningRepository;
    private final ContactRepository contactRepository;

    public List<QuestioningAccreditation> findByContactIdentifier(String id) {
        return questioningAccreditationRepository.findByIdContact(id);
    }

    @Override
    public boolean hasAccreditation(UUID questioningId, String contactId) {
        Optional<QuestioningAccreditation> accreditation = questioningAccreditationRepository
                .findByQuestioning_IdAndIdContact(questioningId, contactId);
        return accreditation.isPresent();
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
      Campaign campaign) {

    findOrCreateAndSaveAccreditation(questioning, isMain, contact, date);
    logContactAccreditationGainUpdate(contact, questioning.getSurveyUnit().getIdSu(), payload, campaign);
  }

    /**
     * Finds an existing accreditation or creates a new one, then configures and saves it.
     * This is the core method for accreditation persistence.
     * @return The persisted QuestioningAccreditation entity.
     */
    private QuestioningAccreditation findOrCreateAndSaveAccreditation(Questioning questioning,
        boolean isMain,
        Contact contact,
        Date date) {
      QuestioningAccreditation qa = questioningAccreditationRepository
          .findAccreditationsByQuestioningIdAndIdContactAndIsMainFalse(questioning.getId(), contact.getIdentifier())
          .orElseGet(() -> {
            QuestioningAccreditation created = new QuestioningAccreditation();
            created.setIdContact(contact.getIdentifier());
            created.setCreationDate(date);
            created.setQuestioning(questioning);
            return created;
          });

      qa.setMain(isMain);
      return questioningAccreditationRepository.save(qa);
    }


  @Override
    public void setQuestioningAccreditationAsMain(QuestioningAccreditation qa, Contact contact, JsonNode eventPayload)
    {
        qa.setMain(true);
        questioningAccreditationRepository.save(qa);
        ContactEvent contactEvent = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, eventPayload);
        contactEventService.saveContactEvent(contactEvent);
    }

  private AccreditationContext prepareAccreditationContext(String contactId, UUID questioningId) {
    Questioning questioning = questioningRepository.findById(questioningId)
        .orElseThrow(() -> new NotFoundException(String.format("Missing Questioning with id %s", questioningId)));

    Contact contact = contactRepository.findById(contactId)
        .orElseThrow(() -> new NotFoundException(String.format("Missing contact with id %s", contactId)));

    Optional<QuestioningAccreditation> existingMainAccreditation = questioningAccreditationRepository
        .findAccreditationsByQuestioningIdAndIsMainTrue(questioningId);

    return new AccreditationContext(questioning, contact, existingMainAccreditation);
  }


  @Override
  public void setMainQuestioningAccreditationToContact(String contactId, UUID questioningId) {
    AccreditationContext context = prepareAccreditationContext(contactId, questioningId);

    Date date = Date.from(Instant.now());
    Campaign campaign = partitioningService.findById(context.questioning().getIdPartitioning()).getCampaign();
    JsonNode payload = ServiceJsonUtil.createPayload("platine-pilotage");

    context.existingMainAccreditation().ifPresentOrElse(
        accreditation -> updateExistingMainAccreditationToNewContact(accreditation, context.contact(), context.questioning().getSurveyUnit().getIdSu(), payload, campaign),
        () -> createQuestioningAccreditation(context.questioning(), true, context.contact(), payload, date, campaign));
  }

  @Override
  public void assignMainAccreditationForNewContact(String contactId, UUID questioningId) {
    AccreditationContext context = prepareAccreditationContext(contactId, questioningId);

    context.existingMainAccreditation().ifPresent(accreditation -> {
      if (accreditation.getIdContact().equals(context.contact().getIdentifier())) {
        return;
      }
      questioningAccreditationRepository.deleteById(accreditation.getId());
    });

    findOrCreateAndSaveAccreditation(context.questioning(), true, context.contact(), Date.from(Instant.now()));
  }

    @Override
    public void updateExistingMainAccreditationToNewContact(QuestioningAccreditation existingAccreditation,
                                                            Contact newContact,
                                                            String surveyUnitId,
                                                            JsonNode payload,
                                                            Campaign campaign)  {

        if(existingAccreditation.getIdContact().equals(newContact.getIdentifier()))
        {
            return;
        }

        Contact previousContact = contactRepository.findById(existingAccreditation.getIdContact())
                .orElseThrow(() -> new NotFoundException(String.format("Missing contact with id %s", existingAccreditation.getIdContact())));

        questioningAccreditationRepository.deleteById(existingAccreditation.getId());
        logContactAccreditationLossUpdate(previousContact, surveyUnitId, payload, campaign);

        createQuestioningAccreditation(existingAccreditation.getQuestioning(), true, newContact, payload, new Date(), campaign);
    }

    @Override
    public void logContactAccreditationLossUpdate(Contact contact,
                                                  String surveyUnitId,
                                                  JsonNode payload,
                                                  Campaign campaign
    ) {


            List<View> viewsToDelete = viewService.findByIdentifierAndIdSuAndCampaignId(
                    contact.getIdentifier(),
                    surveyUnitId,
                    campaign.getId());

            viewsToDelete.forEach(viewService::deleteView);

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
                surveyUnitId);
    }

    @Override
    public void logContactAccreditationGainUpdate(Contact contact,
                                                  String surveyUnitId,
                                                  JsonNode payload,
                                                  Campaign campaign
    ) {

        ContactEvent contactEvent = contactEventService.createContactEvent(contact, ContactEventTypeEnum.update, payload);
        contactEventService.saveContactEvent(contactEvent);

        contactSourceService.saveContactSource(
                contact.getIdentifier(),
                campaign.getSurvey().getSource().getId(),
                surveyUnitId,
                true);

        viewService.createViewAndDeleteEmptyExistingOnesByIdentifier(
                contact.getIdentifier(),
                surveyUnitId,
                campaign.getId());
    }

}
