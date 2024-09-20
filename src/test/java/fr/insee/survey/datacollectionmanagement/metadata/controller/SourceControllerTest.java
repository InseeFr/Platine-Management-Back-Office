package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.config.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        this.mockMvc.perform(get(Constants.API_SOURCES_ID, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getSourceNotFound() throws Exception {
        String identifier = "SOURCENOTFOUND";
        this.mockMvc.perform(get(Constants.API_SOURCES_ID, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void getSourcesOk() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("totalElements", sourceRepository.count());
        jo.put("numberOfElements", sourceRepository.count());

        this.mockMvc.perform(get(Constants.API_SOURCES)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(jo.toString(), false));
    }

    @Test
    void putSourceCreateUpdateDelete() throws Exception {
        String identifier = "SOURCEPUT";

        // create source - status created
        Source source = initSource(identifier);
        String jsonSource = createJson(source);
        mockMvc.perform(
                        put(Constants.API_SOURCES_ID, identifier).content(jsonSource)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonSource.toString(), false));
        assertDoesNotThrow(() -> sourceService.findById(identifier));

        Source sourceFound = sourceService.findById(identifier);
        assertEquals(source.getLongWording(), sourceFound.getLongWording());
        assertEquals(source.getShortWording(), sourceFound.getShortWording());
        assertEquals(source.getPeriodicity(), sourceFound.getPeriodicity());

        // update source - status ok
        source.setLongWording("Long wording update");
        String jsonSourceUpdate = createJson(source);
        mockMvc.perform(put(Constants.API_SOURCES_ID, identifier).content(jsonSourceUpdate)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(jsonSourceUpdate.toString(), false));
        assertDoesNotThrow(() -> sourceService.findById(identifier));
        Source sourceFoundAfterUpdate = sourceService.findById(identifier);
        assertEquals("Long wording update", sourceFoundAfterUpdate.getLongWording());
        assertEquals(source.getId(), sourceFoundAfterUpdate.getId());

        // delete source
        mockMvc.perform(delete(Constants.API_SOURCES_ID, identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> sourceService.findById(identifier));

        // delete source not found
        mockMvc.perform(delete(Constants.API_SOURCES + "/" + identifier).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void putSourcesErrorId() throws Exception {
        String identifier = "NEWONE";
        String otherIdentifier = "WRONG";
        Source source = initSource(identifier);
        String jsonSource = createJson(source);
        mockMvc.perform(put(Constants.API_SOURCES + "/" + otherIdentifier).content(jsonSource)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(content().json(JsonUtil.createJsonErrorBadRequest("id and source id don't match")));

    }

    private Source initSource(String identifier) {
        Source sourceMock = new Source();
        sourceMock.setId(identifier);
        sourceMock.setLongWording("Long wording about " + identifier);
        sourceMock.setShortWording("Short wording about " + identifier);
        sourceMock.setPeriodicity(PeriodicityEnum.T);
        sourceMock.setMandatoryMySurveys(true);
        return sourceMock;
    }

    private String createJson(Source source) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", source.getId());
        jo.put("longWording", source.getLongWording());
        jo.put("shortWording", source.getShortWording());
        jo.put("periodicity", source.getPeriodicity());
        return jo.toString();
    }

}
