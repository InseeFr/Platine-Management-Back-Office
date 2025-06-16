package fr.insee.survey.datacollectionmanagement.questioning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
class MyQuestioningsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void myQuestionnairesNotExist() throws Exception {
        String identifier = "CONT500";
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser(identifier, AuthorityRoleEnum.RESPONDENT));

        MvcResult result = this.mockMvc.perform(get(UrlConstants.API_MY_QUESTIONNAIRES, identifier)).andDo(print())
                .andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        MyQuestionnaireDto[] myQuestionnaires = new ObjectMapper().readValue(json, MyQuestionnaireDto[].class);
        System.out.println(json);
        assertEquals(0, myQuestionnaires.length);
    }
}
