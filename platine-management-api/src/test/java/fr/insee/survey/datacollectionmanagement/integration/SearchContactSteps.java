package fr.insee.survey.datacollectionmanagement.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDtoImpl;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactParamEnum;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class SearchContactSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private MvcResult mvcResult;
    private Page<SearchContactDtoImpl> pageSearchContact;
    private String role;


    @Given("I am a survey manager")
    public void setRole() {
        role = AuthorityRoleEnum.INTERNAL_USER.name();
        SecurityContextHolder.getContext()
                .setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("USER", AuthorityRoleEnum.valueOf(role)));
    }

    @When("I type {string} in the searching contact area by email")
    public void searchContactByEmail(String param) throws Exception {
        mvcResult = mockMvc.perform(get(UrlConstants.API_CONTACTS_SEARCH)
                        .param("searchParam", param)
                        .param("searchType", ContactParamEnum.EMAIL.getValue()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<SearchContactDtoImpl> contentList = objectMapper.convertValue(result.get("content"), new TypeReference<>() {
        });

        pageSearchContact = new PageImpl<>(contentList);
    }

    @When("I type {string} in the searching contact area by name")
    public void searchContactByName(String param) throws Exception {
        mvcResult = mockMvc.perform(get(UrlConstants.API_CONTACTS_SEARCH)
                        .param("searchParam", param)
                        .param("searchType", ContactParamEnum.NAME.getValue()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<SearchContactDtoImpl> contentList = objectMapper.convertValue(result.get("content"), new TypeReference<>() {
        });

        pageSearchContact = new PageImpl<>(contentList);
    }

    @When("I type {string} in the searching contact area by identifier")
    public void searchContactByIdentifier(String param) throws Exception {
        mvcResult = mockMvc.perform(get(UrlConstants.API_CONTACTS_SEARCH)
                        .param("searchParam", param)
                        .param("searchType", ContactParamEnum.IDENTIFIER.getValue()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        List<SearchContactDtoImpl> contentList = objectMapper.convertValue(result.get("content"), new TypeReference<>() {
        });

        pageSearchContact = new PageImpl<>(contentList);
    }

    @Then("I found the following contacts:")
    public void iShouldSeeTheFollowingContacts(DataTable expectedTable) {
        List<Map<String, String>> expectedRows = expectedTable.asMaps(String.class, String.class);

        for (Map<String, String> expectedRow : expectedRows) {
            String expectedIdep = expectedRow.get("idep");
            String expectedLastname = expectedRow.get("lastname");
            String expectedFirstname = expectedRow.get("firstname");
            String expectedEmail = expectedRow.get("email");

            boolean found = pageSearchContact.getContent().stream()
                    .anyMatch(contact ->
                            StringUtils.equalsIgnoreCase(contact.getIdentifier(), expectedIdep) &&
                                    StringUtils.equalsIgnoreCase(contact.getLastName(), expectedLastname) &&
                                    StringUtils.equalsIgnoreCase(contact.getFirstName(), expectedFirstname) &&
                                    StringUtils.equalsIgnoreCase(contact.getEmail(), expectedEmail)
                    );

            assertTrue(found, "Expected to find contact with idep: " + expectedIdep);
        }
    }

    @Then("I found no contact")
    public void iFoundNothing() {
        assertTrue(pageSearchContact.isEmpty(), "Expected to find no contacts");
    }

}

