package fr.insee.survey.datacollectionmanagement.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class SurveyUnitSteps {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    SurveyUnitDetailsDto surveyUnitDetailsDto;

    ResultActions result;

    @Then("I find the surveyUnitDetail with idSu {string}")
    public void iFindTheSurveyUnitDetailWithIdSu(String expectedIdSU) throws Exception {
        result.andExpect(status().isOk());
        MvcResult mvcResult = result.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        SurveyUnitDetailsDto contentDto = objectMapper.readValue(content, SurveyUnitDetailsDto.class);
        Assertions.assertThat(contentDto.getIdSu())
                .isEqualTo(expectedIdSU);
    }

    @Then("I find the surveyUnitDetail with identificationName {string}")
    public void iFindTheSurveyUnitDetailWithIdentificationName(String expectedIdentificationName) throws UnsupportedEncodingException, JsonProcessingException {
        MvcResult mvcResult = result.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        SurveyUnitDetailsDto contentDto = objectMapper.readValue(content, SurveyUnitDetailsDto.class);
        Assertions.assertThat(contentDto.getIdentificationName())
                .isEqualTo(expectedIdentificationName);
    }

    @When("I get survey unit details by idSu {string}")
    public void iGetSurveyUnitDetailsByIdSu(String idSu) throws Exception {
        result = mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID,idSu));
    }
}
