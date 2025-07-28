package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class SearchQuestioningControllerTest {

    @Autowired
    QuestioningService questioningService;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void testSearchWithoutParams() throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    public void testSearchWithParams() throws Exception {
        String json = """
                {
                  "campaignIds": [
                    "SOURCE12023T01"
                  ]
                }
                """;
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    public void testPaginationAndSorting() throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .param("page", "1")
                        .param("pageSize", "5")
                        .param("sortBy", "score")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }



}
