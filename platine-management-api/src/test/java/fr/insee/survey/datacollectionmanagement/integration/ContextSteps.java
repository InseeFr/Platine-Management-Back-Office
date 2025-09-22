package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ContextSteps {

    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    CampaignRepository campaignRepository;
    @Autowired
    PartitioningRepository partitioningRepository;
    @Autowired
    SurveyUnitRepository surveyUnitRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    QuestioningRepository questioningRepository;
    @Autowired
    QuestioningAccreditationRepository questioningAccreditationRepository;
    @Autowired
    ViewRepository viewRepository;

    @Transactional
    @Given("the source {string}")
    public void createSource(String sourceId) {
        Source source = new Source();
        source.setId(sourceId);
        source.setMandatoryMySurveys(false);
        sourceRepository.save(source);
    }

    @Transactional
    @Given("the survey {string} related to source {string}")
    public void createSurvey(String surveyId, String sourceId) {
        Survey survey = new Survey();
        survey.setId(surveyId);
        Source source = sourceRepository.findById(sourceId).orElseThrow(() -> new IllegalArgumentException("Source not found"));
        survey.setSource(source);
        surveyRepository.save(survey);
    }

    @Transactional
    @Given("the campaign {string} related to survey {string}")
    public void createCampaign(String campaignId, String surveyId) {
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        campaign.setSurvey(survey);
        campaignRepository.save(campaign);
    }

    @Transactional
    @Given("the partitioning {string} related to campaign {string}")
    public void createPartitioning(String partId, String campaignId) {
        Partitioning part = new Partitioning();
        part.setId(partId);
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        part.setCampaign(campaign);
        partitioningRepository.save(part);
    }

    @Transactional
    @Given("the survey unit {string} with label {string}")
    public void createSurveyUnit(String idSu, String label) {
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(idSu);
        su.setLabel(label);
        surveyUnitRepository.save(su);
    }

    @Transactional
    @Given("the survey unit {string} with label {string} and identificationName {string} and identificationCode {string}")
    public void createSurveyUnit(String idSu, String label, String identificationName, String identificationCode) {
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(idSu);
        su.setLabel(label);
        su.setIdentificationName(identificationName);
        su.setIdentificationCode(identificationCode);
        surveyUnitRepository.save(su);
    }

    @Given("the contact {string} with firstname {string} and lastname {string} and gender {string} and the streetnumber {string}")
    public void createContact(String contactId, String firstName, String lastName, String gender, String streetNumber) {
        Contact c = new Contact();
        c.setIdentifier(contactId);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setGender(GenderEnum.fromStringIgnoreCase(gender));
        Address address = new Address();
        address.setStreetNumber(streetNumber);
        addressRepository.save(address);
        c.setAddress(address);
        contactRepository.save(c);
    }

    @Transactional
    @Given("the following contacts exist:")
    public void createContacts(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            Contact contact = new Contact();
            contact.setIdentifier(row.get("idep"));
            contact.setFirstName(row.get("firstname"));
            contact.setLastName(row.get("lastname"));
            contact.setEmail(row.get("email"));
            contactRepository.save(contact);
        }
    }


    @Transactional
    @Given("the questioning for partitioning {string} survey unit id {string} and model {string} and main contact {string}")
    public void createQuestioningMainContact(String partId, String idSu, String model, String mainContactId) {
        createQuestioningContact(partId, idSu, model, mainContactId, true);

    }

    @Transactional
    @Given("the questioning for partitioning {string} survey unit id {string} and model {string} and contact {string}")
    public void createQuestioningContact(String partId, String idSu, String model, String mainContactId) {
        createQuestioningContact(partId, idSu, model, mainContactId, false);
    }

    private void createQuestioningContact(String partId, String idSu, String model, String contactId, boolean isMain) {
        Optional<Questioning> q = questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(partId, idSu);
        Questioning questioning;

        if (q.isEmpty()) {
            questioning = new Questioning();
            questioning.setId(UUID.randomUUID());
            questioning.setIdPartitioning(partId);
            questioning.setModelName(model);
            questioning = questioningRepository.save(questioning);
        } else {
            questioning = q.get();
        }
        SurveyUnit su = surveyUnitRepository.findById(idSu).orElseThrow(() -> new IllegalArgumentException("Survey Unit not found"));
        UUID questioningId = questioning.getId();
        List<QuestioningAccreditation> linkedAccreditations = questioningAccreditationRepository
                .findByIdContact(contactId)
                .stream()
                .filter(acc -> acc.getQuestioning().getId().equals(questioningId))
                .toList();
        if (linkedAccreditations.isEmpty()) {
            QuestioningAccreditation qa = new QuestioningAccreditation();
            qa.setQuestioning(questioning);
            qa.setIdContact(contactId);
            qa.setMain(isMain);

            Set<Questioning> setQuestioningSu = su.getQuestionings();
            setQuestioningSu.add(questioning);
            su.setQuestionings(setQuestioningSu);
            surveyUnitRepository.save(su);
            questioningRepository.save(questioning);
            questioningAccreditationRepository.save(qa);
            Set<QuestioningAccreditation> setQuestioningAcc = new HashSet<>();
            setQuestioningAcc.add(qa);
            questioning.setQuestioningAccreditations(setQuestioningAcc);
            questioning.setSurveyUnit(su);
            questioningRepository.save(questioning);
            initOneView(qa);
        }
    }

    private void initOneView(QuestioningAccreditation a) {
        Partitioning p = partitioningRepository.findById(a.getQuestioning().getIdPartitioning()).orElseThrow(() -> new IllegalArgumentException("Contact not found for ID: " + a.getQuestioning().getIdPartitioning()));
        View view = new View();
        view.setIdentifier(contactRepository.findById(a.getIdContact()).orElseThrow(() -> new IllegalArgumentException("Contact not found for ID: " + a.getIdContact())).getIdentifier());
        view.setCampaignId(p.getCampaign().getId());
        view.setIdSu(a.getQuestioning().getSurveyUnit().getIdSu());
        viewRepository.save(view);
    }

    @Transactional
    @Given("the following survey units exist")
    public void createSurveyUnits(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            SurveyUnit surveyUnit = new SurveyUnit();
            surveyUnit.setIdSu(row.get("IDmetier"));
            surveyUnit.setIdentificationName(row.get("Raison sociale"));
            surveyUnit.setIdentificationCode(row.get("IDmetier"));
            surveyUnitRepository.save(surveyUnit);
        }
    }


}
