package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MoogControllerTest {

    @Autowired
    MockMvc mockMvc;
    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    
    @Test
    void getMoogReadOnlyUrl() throws Exception {
        String idCampaign = "SOURCE12023T01";
        String surveyUnitId = "100000000";
        this.mockMvc.perform(get(UrlConstants.MOOG_API_READONLY_URL, idCampaign, surveyUnitId)).andDo(print()).andExpect(status().isOk())

                .andExpect(content().string("http://localhost:8090/lunatic/normal/v3/review/interrogations/bbbbbbbb-bbbb-bbbb-bbbb-000000000000"));
    }


    @Test
    void getMoogReadOnlyUrlCampaignNotFound() throws Exception {
        String idCampaign = "CAMPAIGN";
        String surveyUnitId = "100000000";
        this.mockMvc.perform(get(UrlConstants.MOOG_API_READONLY_URL, idCampaign, surveyUnitId)).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void getMoogReadOnlyUrlQuestioningNotFound() throws Exception {
        String idCampaign = "SOURCE12023T01";
        String surveyUnitId = "SU";
        this.mockMvc.perform(get(UrlConstants.MOOG_API_READONLY_URL, idCampaign, surveyUnitId)).andDo(print()).andExpect(status().isNotFound());
    }
}
