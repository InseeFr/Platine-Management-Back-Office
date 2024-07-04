package fr.insee.survey.datacollectionmanagement.query;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class QuestioningInformationsSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    MvcResult mvcResult;

    //Authentication authentication;

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private PartitioningRepository partitioningRepository;
    @Autowired
    private SurveyUnitRepository surveyUnitRepository;
    @Autowired
    private QuestioningRepository questioningRepository;
    @Autowired
    private QuestioningAccreditationRepository questioningAccreditationRepository;
    @Autowired
    private ContactRepository contactRepository;


    @Given("the source {string}")
    public void createSource(String sourceId) {
        Source source = new Source();
        source.setId(sourceId);
        sourceRepository.save(source);
    }

    @Given("the survey {string} related to source {string}")
    public void createSurvey(String surveyId, String sourceId) {
        Survey survey = new Survey();
        survey.setId(surveyId);
        Source source = sourceRepository.findById(sourceId).orElseThrow(() -> new IllegalArgumentException("Source not found"));
        survey.setSource(source);
        surveyRepository.save(survey);
    }

    @Given("the campaign {string} related to survey {string}")
    public void createCampaign(String campaignId, String surveyId) {
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        campaign.setSurvey(survey);
        campaignRepository.save(campaign);
    }

    @Given("the partitioning {string} related to campaign {string}")
    public void createPartitioning(String partId, String campaignId) {
        Partitioning part = new Partitioning();
        part.setId(partId);
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
        part.setCampaign(campaign);
        partitioningRepository.save(part);
    }

    @Given("the survey unit {string} with label {string}")
    public void createSurveyUnit(String idSu, String label) {
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(idSu);
        su.setLabel(label);
        surveyUnitRepository.save(su);
    }

    @Given("the contact {string} with firstname {string} and lastanme {string} and gender {string}")
    public void createContact(String contactId, String firstName, String lastName, String gender) {
        Contact c = new Contact();
        c.setIdentifier(contactId);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setGender(Contact.Gender.valueOf(gender));
        contactRepository.save(c);
    }

    @Given("the questioning for partitioning {string} survey unit id {string} and model {string} and main contact {string}")
    public void createQuestioning(String partId, String idSu, String model, String mainContactId) {
        Questioning q = new Questioning();
        q.setIdPartitioning(partId);
        q.setModelName(model);
        questioningRepository.save(q);
        SurveyUnit su = surveyUnitRepository.findById(idSu).orElseThrow(() -> new IllegalArgumentException("Survey Unit not found"));
        q.setSurveyUnit(su);
        QuestioningAccreditation qa = new QuestioningAccreditation();
        qa.setQuestioning(q);
        qa.setIdContact(mainContactId);
        qa.setMain(true);
        q.setQuestioningAccreditations(Set.of(qa));
        questioningRepository.save(q);
        questioningAccreditationRepository.save(qa);
    }

    @Given("the user {string} is authenticated as {string}")
    public void theUserIsAuthenticatedAs(String contactId, String role) {
       // authentication = AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.valueOf(role));
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.valueOf(role)));


    }

    @Given("the user is authenticated as {string}")
    public void the_user_is_authenticated_as(String role) {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.valueOf(role)));
    }

    @When("a GET request is made to {string} with campaign id {string}, survey unit id {string} and role {string}")
    public void aGETRequestIsMadeToWithCampaignIdSurveyUnitIdAndRole(String url, String idCampaign, String idsu, String role) throws Exception {
        mvcResult = mockMvc.perform(get(url, idCampaign, idsu)
                        .param("role", role)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
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

