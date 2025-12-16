package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitAddress;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SurveyUnitControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SurveyUnitService surveyUnitService;

    @Autowired
    SurveyUnitRepository surveyUnitRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider
                .getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getSurveyUnitOk() throws Exception {
        String identifier = "100000000";
        SurveyUnit surveyUnit = surveyUnitService.findbyId(identifier);
        String json = createJson(surveyUnit);
        String response = this.mockMvc
                .perform(get(UrlConstants.API_SURVEY_UNITS_ID, identifier))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(json, response, JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Should return survey unit's campaigns")
    void should_return_survey_unit_campaigns() throws Exception {
        // given
        String identifier = "100000000";

        // when & then
        String jsonResult = this.mockMvc
               .perform(get(UrlConstants.API_SURVEY_UNITS_ID_CAMPAIGNS, identifier))
               .andDo(print())
               .andExpect(status().isOk())
               .andReturn()
               .getResponse()
               .getContentAsString();

        String expectedJson = """
                [
                    "SOURCE12023T01"
                ]
                """;
        JSONAssert.assertEquals(expectedJson, jsonResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void getSurveyUnitNotFound() throws Exception {
        String identifier = "900000000";
        this.mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getSurveyUnitsOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", surveyUnitRepository.count());
        jo.put("numberOfElements", surveyUnitRepository.count());

        String response = this.mockMvc
                .perform(get(UrlConstants.API_SURVEY_UNITS))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(jo.toString(), response, JSONCompareMode.LENIENT);
    }

    @Test
    void putSurveyUnitCreateUpdateDelete() throws Exception {
        String identifier = "TESTPUT";

        // create surveyUnit - status created
        SurveyUnit surveyUnit = initSurveyUnit(identifier);
        String jsonSurveyUnit = createJson(surveyUnit);
        String response = mockMvc.perform(
                        put(UrlConstants.API_SURVEY_UNITS_ID, identifier).content(jsonSurveyUnit)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(jsonSurveyUnit, response, JSONCompareMode.LENIENT);
        SurveyUnit surveyUnitFound = surveyUnitService.findbyId(identifier);
        assertEquals(surveyUnit.getIdSu(), surveyUnitFound.getIdSu());
        assertEquals(surveyUnit.getIdentificationCode(), surveyUnitFound.getIdentificationCode());
        assertEquals(surveyUnit.getIdentificationName(), surveyUnitFound.getIdentificationName());

        // update surveyUnit - status ok
        surveyUnit.setIdentificationName("identificationNameUpdate");
        String jsonSurveyUnitUpdate = createJson(surveyUnit);
        response = mockMvc.perform(put(UrlConstants.API_SURVEY_UNITS_ID, identifier).content(jsonSurveyUnitUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(jsonSurveyUnitUpdate, response, JSONCompareMode.LENIENT);
        SurveyUnit surveyUnitFoundAfterUpdate = surveyUnitService.findbyId(identifier);
        assertEquals("identificationNameUpdate", surveyUnitFoundAfterUpdate.getIdentificationName());
        assertEquals(surveyUnit.getIdSu(), surveyUnitFoundAfterUpdate.getIdSu());
        assertEquals(surveyUnit.getIdentificationName(), surveyUnitFoundAfterUpdate.getIdentificationName());

        // delete surveyUnit
        surveyUnitService.deleteSurveyUnit(identifier);
        assertThrows(NotFoundException.class, () -> surveyUnitService.findbyId(identifier));


    }

    @Test
    void putSurveyUnitAddressCreateUpdateDelete() throws Exception {
        String identifier = "TESTADDRESS";

        // create surveyUnit - status created
        SurveyUnit surveyUnit = initSurveyUnitAddress(identifier);
        String jsonSurveyUnit = createJsonSurveyUnitAddress(surveyUnit);
        String response = mockMvc.perform(
                        put(UrlConstants.API_SURVEY_UNITS_ID, identifier).content(jsonSurveyUnit)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(jsonSurveyUnit, response, JSONCompareMode.LENIENT);
        SurveyUnit suFound = surveyUnitService.findbyId(identifier);
        assertEquals(surveyUnit.getSurveyUnitAddress().getCityName(), suFound.getSurveyUnitAddress().getCityName());

        // update surveyUnit - status ok
        String newCityName = "cityUpdate";
        surveyUnit.getSurveyUnitAddress().setCityName(newCityName);
        String jsonSurveyUnitUpdate = createJsonSurveyUnitAddress(surveyUnit);
        response = mockMvc.perform(put(UrlConstants.API_SURVEY_UNITS_ID, identifier).content(jsonSurveyUnitUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(jsonSurveyUnitUpdate, response, JSONCompareMode.LENIENT);
        SurveyUnit countactFoundAfterUpdate = surveyUnitService.findbyId(identifier);
        assertEquals(surveyUnit.getSurveyUnitAddress().getCityName(),
                countactFoundAfterUpdate.getSurveyUnitAddress().getCityName());

        // delete surveyUnit
        surveyUnitService.deleteSurveyUnit(identifier);
        assertThrows(NotFoundException.class, () -> surveyUnitService.findbyId(identifier));

    }

    @Test
    void putSurveyUnitsErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        SurveyUnit surveyUnit = initSurveyUnit(identifier);
        String jsonSurveyUnit = createJson(surveyUnit);
        mockMvc.perform(put(UrlConstants.API_SURVEY_UNITS_ID, otherIdentifier).content(jsonSurveyUnit)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and idSu don't match")));

    }

    private SurveyUnit initSurveyUnit(String identifier) {
        SurveyUnit surveyUnitMock = new SurveyUnit();
        surveyUnitMock.setIdSu(identifier);
        surveyUnitMock.setIdentificationCode("CODE - " + identifier);
        surveyUnitMock.setIdentificationName("company name " + identifier);

        return surveyUnitMock;
    }

    private SurveyUnit initSurveyUnitAddress(String identifier) {
        SurveyUnit surveyUnit = initSurveyUnit(identifier);
        SurveyUnitAddress address = initAddress(identifier);
        surveyUnit.setSurveyUnitAddress(address);
        return surveyUnit;
    }

    private SurveyUnitAddress initAddress(String identifier) {
        SurveyUnitAddress address = new SurveyUnitAddress();
        address.setCityName("city " + identifier);
        address.setCountryName("country " + identifier);
        address.setStreetName("steet " + identifier);
        address.setStreetNumber(identifier);
        return address;
    }

    private String createJson(SurveyUnit surveyUnit) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("idSu", surveyUnit.getIdSu());
        jo.put("identificationCode", surveyUnit.getIdentificationCode());
        jo.put("identificationName", surveyUnit.getIdentificationName());
        return jo.toString();
    }

    private String createJsonSurveyUnitAddress(SurveyUnit surveyUnit) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("idSu", surveyUnit.getIdSu());
        jo.put("identificationCode", surveyUnit.getIdentificationCode());
        jo.put("identificationName", surveyUnit.getIdentificationName());
        jo.put("address", createJsonAddress(surveyUnit));
        return jo.toString();

    }

    private JSONObject createJsonAddress(SurveyUnit surveyUnit) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("cityName", surveyUnit.getSurveyUnitAddress().getCityName());
        jo.put("streetName", surveyUnit.getSurveyUnitAddress().getStreetName());
        jo.put("countryName", surveyUnit.getSurveyUnitAddress().getCountryName());
        return jo;
    }

}
