package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class QuestionningEventControllerTest {

    @Autowired
    QuestioningService questioningService;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }


    @Test
    @Transactional
    void getQuestioningEventOk() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        Long id = questioning.getQuestioningAccreditations().stream().findFirst().get().getId();
        String json = createJsonQuestioningEvent(id);
        this.mockMvc.perform(get(Constants.API_QUESTIONING_ID_QUESTIONING_EVENTS, id)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getQuestioningEventNotFound() throws Exception {
        String identifier = "300";
        this.mockMvc.perform(get(Constants.API_QUESTIONING_ID_QUESTIONING_EVENTS, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    private String createJsonQuestioningEvent(Long identifier) throws JSONException {
        JSONObject joEventInitla = new JSONObject();
        joEventInitla.put("type", "INITLA");

        JSONObject joPartiel = new JSONObject();
        joPartiel.put("type", "PARTIELINT");

        JSONObject joValint = new JSONObject();
        joValint.put("type", "VALINT");

        JSONArray ja = new JSONArray();
        ja.put(joEventInitla);
        ja.put(joPartiel);
        ja.put(joValint);

        System.out.println(ja.toString());
        return ja.toString();
    }

}
