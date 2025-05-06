package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class QuestioningControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    private static RequestPostProcessor jwtWithRole(AuthorityRoleEnum role) {
        return jwt().authorities(role::securityRole);
    }

    // === /api/survey-units/{id}/questionings ===
    @Test
    void getQuestioningsBySurveyUnit_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, "100000000")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getQuestioningsBySurveyUnit_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, "100000000")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole(AuthorityRoleEnum.PORTAL)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getQuestioningsBySurveyUnit_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, "100000000")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole(AuthorityRoleEnum.ADMIN)))
                .andExpect(status().isOk());
    }

    // === /api/questionings/{id}/assistance ===
    @Test
    void getAssistance_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGS_ID_ASSISTANCE, 1L)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAssistance_200_asPortal() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGS_ID_ASSISTANCE, 1L)
                        .with(jwtWithRole(AuthorityRoleEnum.PORTAL)))
                .andExpect(status().isOk());
    }

    @Test
    void getAssistance_200_asAdmin() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGS_ID_ASSISTANCE, 1L)
                        .with(jwtWithRole(AuthorityRoleEnum.ADMIN)))
                .andExpect(status().isOk());
    }

    // === /api/questionings-id?campaignId=X&surveyUnitId=Y ===
    @Test
    void getQuestioningId_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGSID)
                        .param("campaignId", "SOURCE12023T01")
                        .param("surveyUnitId", "100000000")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getQuestioningId_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGSID)
                        .param("campaignId", "SOURCE12023T01")
                        .param("surveyUnitId", "100000000")
                        .with(jwtWithRole(AuthorityRoleEnum.PORTAL)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getQuestioningId_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_QUESTIONINGSID)
                        .param("campaignId", "SOURCE12023T01")
                        .param("surveyUnitId", "100000000")
                        .with(jwtWithRole(AuthorityRoleEnum.ADMIN)))
                .andExpect(status().isOk());
    }





}
