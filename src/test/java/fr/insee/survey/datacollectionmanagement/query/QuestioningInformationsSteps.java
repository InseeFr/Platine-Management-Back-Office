package fr.insee.survey.datacollectionmanagement.query;

import fr.insee.survey.datacollectionmanagement.config.TestUserProvider;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class QuestioningInformationsSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    MvcResult mvcResult;
    Authentication auth;

    TestUserProvider userProvider;

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
        Source source = new Source();
        source.setId(sourceId);
        survey.setSource(source);
        surveyRepository.save(survey);
    }

    @Given("the campaign {string} related to survey {string}")
    public void createCampaign(String campaignId, String surveyId) {
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        Survey survey = new Survey();
        survey.setId(surveyId);
        campaign.setSurvey(survey);
        campaignRepository.save(campaign);
    }

    @Given("the partitioning {string} related to campaign {string}")
    public void createPartitioning(String partId, String campaignId) {
        Partitioning part = new Partitioning();
        part.setId(partId);
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
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
        SurveyUnit su = surveyUnitRepository.findById(idSu).get();
        q.setSurveyUnit(su);
        QuestioningAccreditation qa = new QuestioningAccreditation();
        qa.setQuestioning(q);
        qa.setIdContact(mainContactId);
        qa.setMain(true);
        q.setQuestioningAccreditations(Set.of(qa));
        questioningRepository.save(q);
        questioningAccreditationRepository.save(qa);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


    }


    @Given("the user {string} is authenticated as {string}")
    public void theUserIsAuthenticatedAs(String contactId, String role) {

        userProvider = new TestUserProvider(contactId, List.of(role));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @When("a GET request is made to {string} with campaign id {string}, survey unit id {string} and role {string}")
    public void aGETRequestIsMadeToWithCampaignIdSurveyUnitIdAndRole(String url, String idCampaign, String idsu, String role) throws Exception {

        mvcResult = mockMvc.perform(get(url, idCampaign, idsu)
                        .param("role", role)
                        .accept(MediaType.APPLICATION_XML)
                        //.with(authentication???)
                )
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

