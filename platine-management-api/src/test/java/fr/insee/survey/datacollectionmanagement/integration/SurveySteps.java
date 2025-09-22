package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SurveySteps {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SourceRepository sourceRepository;

    private final String surveyId = "SURVEY-ID";
  private ResultActions resultActions;

    @Transactional
    @Given("a survey exists")
    public void createSurvey() {
        Survey survey = new Survey();
        survey.setId(surveyId);
        survey.setCnisUrl("cnisUrl");
        survey.setCommunication("communication");
        survey.setLongWording("longWording");
        survey.setShortWording("shortWording");
        survey.setSampleSize(10);
        survey.setYear(2024);
        survey.setDiffusionUrl("diffusionUrl");
        survey.setLongObjectives("longObjectives");
        survey.setShortObjectives("shortObjectives");
        survey.setSpecimenUrl("specimenUrl");
        survey.setNoticeUrl("noticeUrl");
        survey.setVisaNumber("visa");
        Source source = new Source();
        source.setId("SOURCE-ID");
        source.setMandatoryMySurveys(false);
        source = sourceRepository.save(source);

        survey.setSource(source);
        surveyRepository.save(survey);
    }

    @Given("I am an authenticated user")
    public void setRole() {
      String role = AuthorityRoleEnum.INTERNAL_USER.name();
        SecurityContextHolder.getContext()
                .setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("USER", AuthorityRoleEnum.valueOf(
                    role)));
    }

    @When("I'm searching the existing survey")
    public void searchSurveyById() throws Exception {
        resultActions = mockMvc.perform(get("/api/surveys/" + surveyId));
    }

    @Then("the survey is returned")
    public void surveyReturned() throws Exception {
        MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        String expectedContent = """
        {
          "id": "SURVEY-ID",
          "cnisUrl": "cnisUrl",
          "communication": "communication",
          "longWording": "longWording",
          "shortWording": "shortWording",
          "sampleSize": 10,
          "year": 2024,
          "diffusionUrl": "diffusionUrl",
          "longObjectives": "longObjectives",
          "shortObjectives": "shortObjectives",
          "specimenUrl": "specimenUrl",
          "noticeUrl": "noticeUrl",
          "visaNumber": "visa",
          "sourceId": "SOURCE-ID"
        }
        """;
        JSONAssert.assertEquals(expectedContent, content, JSONCompareMode.LENIENT);
    }
}
