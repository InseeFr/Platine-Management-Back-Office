package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WebclientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getQuestioning() throws Exception {
        // GIVEN
        final String modelName = "m0";
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(
                get(Constants.API_WEBCLIENT_QUESTIONINGS)
                        .param("modelName", modelName)
                        .param("idPartitioning", idPartitioning)
                        .param("idSurveyUnit", idSurveyUnit)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPartitioning").value(idPartitioning))
                .andExpect(jsonPath("$.modelName").value(modelName))
                .andExpect(jsonPath("$.surveyUnit.idSu").value(idSurveyUnit));
    }

    @Test
    void getMetadata() throws Exception {
        // GIVEN
        final String id = "SOURCE12023T2000";

        // WHEN
        this.mockMvc.perform(get(Constants.API_WEBCLIENT_METADATA_ID, id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.id").value("SOURCE1"));
    }

    @Test
    void getMainContact() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(
                get(Constants.API_MAIN_CONTACT)
                        .param("partitioning", idPartitioning)
                        .param("survey-unit", idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("CONT1"));

        this.mockMvc.perform(
                        get(Constants.API_MAIN_CONTACT)
                                .param("partitioning", idPartitioning)
                                .param("survey-unit", "wrong_id"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
