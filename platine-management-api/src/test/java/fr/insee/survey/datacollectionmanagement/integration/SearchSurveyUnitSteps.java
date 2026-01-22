package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDtoImpl;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitParamEnum;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SearchSurveyUnitSteps {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonMapper jsonMapper;

    @Autowired
    TestSecurityContext testSecurityContext;

    private MvcResult mvcResult;
    private Page<SearchSurveyUnitDtoImpl> pageSearchSurveyUnit;

    @Given("I am a survey manager for survey unit")
    public void setRole() {
        var auth = AuthenticationUserProvider.getAuthenticatedUser(
                "USER",
                AuthorityRoleEnum.INTERNAL_USER
        );
        testSecurityContext.setAuthentication(auth);
    }

    @When("I type {string} in the searching survey unit area by code")
    public void searchSurveyUnitByEmail(String param) throws Exception {
        mvcResult = mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_SEARCH)
                        .with(authentication(testSecurityContext.getAuthentication()))
                        .param("searchParam", param)
                        .param("searchType", SurveyUnitParamEnum.CODE.getValue()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> result = jsonMapper.readValue(content, new TypeReference<>() {});
        List<SearchSurveyUnitDtoImpl> contentList = jsonMapper.convertValue(
                result.get("content"),
                new TypeReference<>() {}
        );
        pageSearchSurveyUnit = new PageImpl<>(contentList);
    }

    @When("I type {string} in the searching survey unit area by name")
    public void searchSurveyUnitByName(String param) throws Exception {
        mvcResult = mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_SEARCH)
                        .with(authentication(testSecurityContext.getAuthentication()))
                        .param("searchParam", param)
                        .param("searchType", SurveyUnitParamEnum.NAME.getValue()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Map<String, Object> result = jsonMapper.readValue(content, new TypeReference<>() {});
        List<SearchSurveyUnitDtoImpl> contentList = jsonMapper.convertValue(
                result.get("content"),
                new TypeReference<>() {}
        );
        pageSearchSurveyUnit = new PageImpl<>(contentList);
    }

    @Then("I found the following SU:")
    public void iShouldSeeTheFollowingQSurveyUnits(DataTable expectedTable) {
        List<Map<String, String>> expectedRows = expectedTable.asMaps(String.class, String.class);

        for (Map<String, String> expectedRow : expectedRows) {
            String expectedCode = expectedRow.get("IDmetier");
            String expectedName = expectedRow.get("Raison sociale");

            boolean found = pageSearchSurveyUnit.getContent().stream()
                    .anyMatch(su ->
                            StringUtils.equalsIgnoreCase(su.getIdentificationCode(), expectedCode) &&
                                    StringUtils.equalsIgnoreCase(su.getIdentificationName(), expectedName)
                    );

            assertTrue(found, "Expected to find survey unit with code: " + expectedCode);
        }
    }

    @Then("I found no survey unit")
    public void iFoundNothing() {
        assertTrue(pageSearchSurveyUnit.isEmpty(), "Expected to find no survey units");
    }
}
