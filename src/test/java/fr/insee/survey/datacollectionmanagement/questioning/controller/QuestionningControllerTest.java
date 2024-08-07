package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.config.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class QuestionningControllerTest {

    @Autowired
    QuestioningService questioningService;
    @Autowired
    MockMvc mockMvc;


    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getQuestioningOk() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        Long id = questioning.getQuestioningAccreditations().stream().findFirst().get().getId();
        String json = createJson(id).toString();
        this.mockMvc.perform(get(Constants.API_QUESTIONINGS_ID, id)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getQuestioningNotFound() throws Exception {
        String id = "300";
        this.mockMvc.perform(get(Constants.API_QUESTIONINGS_ID, id)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getQuestioningsBySurveyUnit() throws Exception {
        String idSu = "100000000";
        String json = createJsonQuestionings(idSu);
        this.mockMvc.perform(get(Constants.API_SURVEY_UNITS_ID_QUESTIONINGS, idSu)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json, false));

    }

    private JSONObject createJson(Long id) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        return jo;
    }

    private String createJsonQuestionings(String id) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("surveyUnitId", id);
        JSONArray ja = new JSONArray();
        ja.put(jo);
        System.out.println(ja.toString());
        return ja.toString();
    }

}
