package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.config.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SurveyService;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }
    @Test
    void getSourcesOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", surveyRepository.count());
        jo.put("numberOfElements", surveyRepository.count());

        this.mockMvc.perform(get(Constants.API_SURVEYS)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(jo.toString(), false));

    }

    @Test
    void getSurveyNotFound() throws Exception {
        String identifier = "SURVEYNOTFOUND";
        this.mockMvc.perform(get(Constants.API_SURVEYS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }


    @Test
    void putSurveyCreateUpdateDelete() throws Exception {
        String identifier = "SURVEYPUT";

        // create survey - status created
        Survey survey = initSurvey(identifier);
        String jsonSurvey = createJson(survey, "SOURCE1");
        mockMvc.perform(
                        put(Constants.API_SURVEYS_ID, identifier).content(jsonSurvey)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonSurvey.toString(), false));

        assertDoesNotThrow(() -> surveyService.findById(identifier));
        Survey surveyFound = surveyService.findById(identifier);
        assertEquals(survey.getLongWording(), surveyFound.getLongWording());
        assertEquals(survey.getShortWording(), surveyFound.getShortWording());
        assertEquals(survey.getSampleSize(), surveyFound.getSampleSize());

        // update survey - status ok
        survey.setLongWording("Long wording update");
        String jsonSurveyUpdate = createJson(survey, "SOURCE1");
        mockMvc.perform(put(Constants.API_SURVEYS_ID, identifier).content(jsonSurveyUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonSurveyUpdate.toString(), false));
        assertDoesNotThrow(() -> surveyService.findById(identifier));
        Survey surveyFoundAfterUpdate = surveyService.findById(identifier);

        assertEquals("Long wording update", surveyFoundAfterUpdate.getLongWording());
        assertEquals(survey.getId(), surveyFoundAfterUpdate.getId());

        // delete survey
        mockMvc.perform(delete(Constants.API_SURVEYS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThrows(NotFoundException.class, () -> surveyService.findById(identifier));

        // delete survey not found
        mockMvc.perform(delete(Constants.API_SURVEYS_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putSurveysErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        Survey survey = initSurvey(identifier);
        String jsonSurvey = createJson(survey, "SOURCE1");
        mockMvc.perform(put(Constants.API_SURVEYS_ID, otherIdentifier).content(jsonSurvey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and idSurvey don't match")));


    }

    private Survey initSurvey(String identifier) {
        Survey surveyMock = new Survey();
        surveyMock.setYear(2023);
        surveyMock.setId(identifier);
        surveyMock.setLongWording("Long wording about " + identifier);
        surveyMock.setShortWording("Short wording about " + identifier);
        surveyMock.setSampleSize(1000);
        return surveyMock;
    }

    private String createJson(Survey survey, String idSource) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", survey.getId());
        jo.put("year", survey.getYear());
        jo.put("sourceId", idSource);
        jo.put("longWording", survey.getLongWording());
        jo.put("shortWording", survey.getShortWording());
        jo.put("sampleSize", survey.getSampleSize());
        return jo.toString();
    }

    @Test
    void getSurveyOk() throws Exception {
        String identifier = "SOURCE12022";
        assertDoesNotThrow(() -> surveyService.findById(identifier));
        Survey survey = surveyService.findById(identifier);

        String json = createJson(survey, "SOURCE1");
        this.mockMvc.perform(get(Constants.API_SURVEYS_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }
}
