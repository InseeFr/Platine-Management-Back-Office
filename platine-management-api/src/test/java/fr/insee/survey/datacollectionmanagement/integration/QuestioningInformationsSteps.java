package fr.insee.survey.datacollectionmanagement.integration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
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
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class QuestioningInformationsSteps {

    @Autowired
    MockMvc mockMvc;
    MvcResult mvcResult;
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
    QuestioningRepository questioningRepository;
    @Autowired
    QuestioningAccreditationRepository questioningAccreditationRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ViewRepository viewRepository;
    private QuestioningInformationsDto questioningInformationsDto;


    @Transactional
    @Given("the source {string}")
    public void createSource(String sourceId) {
        Source source = new Source();
        source.setId(sourceId);
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

    @Given("the contact {string} with firstname {string} and lastname {string} and gender {string} and the streetnumber {string}")
    public void createContact(String contactId, String firstName, String lastName, String gender, String streetNumber) {
        Contact c = new Contact();
        c.setIdentifier(contactId);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setGender(GenderEnum.valueOf(gender));
        Address address = new Address();
        address.setStreetNumber(streetNumber);
        addressRepository.save(address);
        c.setAddress(address);
        contactRepository.save(c);
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
        Questioning q = questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(partId, idSu);
        if (q == null) {
            q = new Questioning();
            q.setIdPartitioning(partId);
            q.setModelName(model);
            q = questioningRepository.save(q);
        }
        final Questioning savedQ = q;
        SurveyUnit su = surveyUnitRepository.findById(idSu).orElseThrow(() -> new IllegalArgumentException("Survey Unit not found"));

        List<QuestioningAccreditation> listAccreditations = questioningAccreditationRepository.findByIdContact(contactId);
        if (listAccreditations.stream().filter(acc -> acc.getQuestioning().getId().equals(savedQ.getId())).toList().isEmpty()) {
            QuestioningAccreditation qa = new QuestioningAccreditation();
            qa.setQuestioning(q);
            qa.setIdContact(contactId);
            qa.setMain(isMain);

            Set<Questioning> setQuestioningSu = su.getQuestionings();
            setQuestioningSu.add(q);
            su.setQuestionings(setQuestioningSu);
            surveyUnitRepository.save(su);
            questioningRepository.save(q);
            questioningAccreditationRepository.save(qa);
            Set<QuestioningAccreditation> setQuestioningAcc = new HashSet<>();
            setQuestioningAcc.add(qa);
            q.setQuestioningAccreditations(setQuestioningAcc);
            q.setSurveyUnit(su);
            questioningRepository.save(q);
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

    @Given("the user {string} is authenticated as {string}")
    public void theUserIsAuthenticatedAs(String contactId, String role) {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.valueOf(role)));


    }

    @Given("the user is authenticated as {string}")
    public void the_user_is_authenticated_as(String role) {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.valueOf(role)));
    }

    @When("a GET request is made to {string} with campaign id {string}, survey unit id {string} and role {string}")
    @WithMockUser(authorities = "ROLE_WEB_CLIENT")
    public void aGETRequestIsMadeToWithCampaignIdSurveyUnitIdAndRole(String url, String idCampaign, String idsu, String role) throws Exception {
        mvcResult = mockMvc.perform(get(url, idCampaign, idsu).param("role", role).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk()).andReturn();
        XmlMapper xmlMapper = new XmlMapper();
        questioningInformationsDto = xmlMapper.readValue(mvcResult.getResponse().getContentAsString(), QuestioningInformationsDto.class);
    }

    @Then("the response XML should have a contact with identity {string}")
    public void theResponseXMLShouldHaveAContactWithIdentity(String identity) {
        assertThat(questioningInformationsDto.getContactInformationsDto().getIdentity()).isEqualTo(identity);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the response content should be XML")
    public void theResponseContentShouldBeXML() {
        assertThat(mvcResult.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_XML_VALUE);
    }
}

