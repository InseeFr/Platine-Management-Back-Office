package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.dataformat.xml.XmlMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestioningInformationsSteps {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private TestSecurityContext testSecurityContext;

    MvcResult mvcResult;

    private QuestioningInformationsDto questioningInformationsDto;

    @Given("the user {string} is authenticated as {string}")
    public void theUserIsAuthenticatedAs(String contactId, String role) {
        testSecurityContext.setAuthentication(
                AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.valueOf(role))
        );
    }

    @Given("the user is authenticated as {string}")
    public void the_user_is_authenticated_as(String role) {
        testSecurityContext.setAuthentication(
                AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.valueOf(role))
        );
    }

    @When("a GET request is made to {string} with campaign id {string}, survey unit id {string} and role {string}")
    public void aGETRequestIsMadeToWithCampaignIdSurveyUnitIdAndRole(
            String url, String idCampaign, String idsu, String role
    ) throws Exception {

        mvcResult = mockMvc.perform(
                        get(url, idCampaign, idsu)
                                .with(authentication(testSecurityContext.getAuthentication()))
                                .param("role", role)
                                .accept(MediaType.APPLICATION_XML)
                )
                .andExpect(status().isOk())
                .andReturn();

        XmlMapper xmlMapper = new XmlMapper();
        questioningInformationsDto = xmlMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                QuestioningInformationsDto.class
        );
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
