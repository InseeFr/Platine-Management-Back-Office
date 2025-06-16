package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SourceTypeEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.util.JsonUtil;
import org.json.JSONArray;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class SourceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SourceService sourceService;

    @Autowired
    SourceRepository sourceRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getSourceOk() throws Exception {
        String identifier = "SOURCE1";
        assertDoesNotThrow(() -> sourceService.findById(identifier));
        Source source = sourceService.findById(identifier);
        String json = createJson(source);
        this.mockMvc.perform(get(UrlConstants.API_SOURCES_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getSourceNotFound() throws Exception {
        String identifier = "SOURCENOTFOUND";
        this.mockMvc.perform(get(UrlConstants.API_SOURCES_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getSourcesOk() throws Exception {
        JSONArray jo = new JSONArray();
        Source source1 = sourceService.findById("SOURCE1");
        jo.put(createJson(source1));
        Source source2 = sourceService.findById("SOURCE2");
        jo.put(createJson(source2));

        this.mockMvc.perform(get(UrlConstants.API_SOURCES)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SOURCE1"))
                .andExpect(jsonPath("$[1].id").value("SOURCE2"))
                .andExpect(jsonPath("$[0].shortWording").value("Short wording of SOURCE1"))
                .andExpect(jsonPath("$[1].shortWording").value("Short wording of SOURCE2"))
                .andExpect(jsonPath("$[0].longWording").value("Long wording of SOURCE1 ?"))
                .andExpect(jsonPath("$[1].longWording").value("Long wording of SOURCE2 ?"));
    }

    @Test
    void putSourceCreateUpdateDelete() throws Exception {
        String identifier = "SOURCEPUT";

        // create source - status created
        Source source = initSource(identifier);
        String jsonSource = createJson(source);
        mockMvc.perform(
                        put(UrlConstants.API_SOURCES_ID, identifier).content(jsonSource)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonSource.toString(), false));
        assertDoesNotThrow(() -> sourceService.findById(identifier));

        Source sourceFound = sourceService.findById(identifier);
        assertEquals(source.getType(), sourceFound.getType());
        assertEquals(source.getLongWording(), sourceFound.getLongWording());
        assertEquals(source.getShortWording(), sourceFound.getShortWording());
        assertEquals(source.getPeriodicity(), sourceFound.getPeriodicity());

        // update source - status ok
        source.setLongWording("Long wording update");
        String jsonSourceUpdate = createJson(source);
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID, identifier).content(jsonSourceUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonSourceUpdate.toString(), false));
        assertDoesNotThrow(() -> sourceService.findById(identifier));
        Source sourceFoundAfterUpdate = sourceService.findById(identifier);
        assertEquals("Long wording update", sourceFoundAfterUpdate.getLongWording());
        assertEquals(source.getId(), sourceFoundAfterUpdate.getId());

        // delete source
        mockMvc.perform(delete(UrlConstants.API_SOURCES_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> sourceService.findById(identifier));

        // delete source not found
        mockMvc.perform(delete(UrlConstants.API_SOURCES + "/" + identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putSourcesErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        Source source = initSource(identifier);
        String jsonSource = createJson(source);
        mockMvc.perform(put(UrlConstants.API_SOURCES + "/" + otherIdentifier).content(jsonSource)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and source id don't match")));

    }

    private Source initSource(String identifier) {
        Source sourceMock = new Source();
        sourceMock.setId(identifier);
        sourceMock.setLongWording("Long wording about " + identifier);
        sourceMock.setShortWording("Short wording about " + identifier);
        sourceMock.setPeriodicity(PeriodicityEnum.T);
        sourceMock.setType(SourceTypeEnum.HOUSEHOLD);
        sourceMock.setMandatoryMySurveys(true);
        return sourceMock;
    }

    private String createJson(Source source) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", source.getId());
        jo.put("longWording", source.getLongWording());
        jo.put("shortWording", source.getShortWording());
        jo.put("type", source.getType());
        jo.put("periodicity", source.getPeriodicity());
        return jo.toString();
    }

}
