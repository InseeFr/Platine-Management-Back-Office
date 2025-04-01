package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.SensitivityEnum;
import org.json.JSONException;
import org.json.JSONObject;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class SourceControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    private static RequestPostProcessor jwtWithRole(String role) {
        return jwt().authorities(() -> "ROLE_" + role);
    }

    // === /api/sources ===
    @Test
    void getSources_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getSources_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES)
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void getSources_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES)
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isOk());
    }

    // === /api/sources/ongoing ===
    @Test
    void getOngoingSources_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ONGOING)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getOngoingSources_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ONGOING)
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isOk());
    }

    // === /api/sources/{id} ===
    @Test
    void getSource_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getSource_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void getSource_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isOk());
    }

    // === PUT /api/sources/{id} ===
    @Test
    void putSource_401() throws Exception {

        mockMvc.perform(put(UrlConstants.API_SOURCES_ID, "SOURCE1").content(createJsonSource())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void putSource_403() throws Exception {
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID, "SOURCE1").content(createJsonSource())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void putSource_2xx() throws Exception {
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID, "SOURCE1").content(createJsonSource())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().is2xxSuccessful());
    }

    // === DELETE /api/sources/{id} ===
    @Test
    void deleteSource_401() throws Exception {
        mockMvc.perform(delete(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void deleteSource_403() throws Exception {
        mockMvc.perform(delete(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void deleteSource_204() throws Exception {
        mockMvc.perform(delete(UrlConstants.API_SOURCES_ID, "SOURCE1")
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isNoContent());
    }

    // === /api/sources/{id}/opened ===
    @Test
    void isSourceOpened_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCE_ID_OPENED, "SOURCE1")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void isSourceOpened_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCE_ID_OPENED, "SOURCE1")
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isOk());
    }

    // === /api/owners/{id}/sources ===
    @Test
    void getSourcesByOwner_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_OWNERS_ID_SOURCES, "OWNER1")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getSourcesByOwner_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_OWNERS_ID_SOURCES, "Insee")
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void getSourcesByOwner_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_OWNERS_ID_SOURCES, "Insee")
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isOk());
    }

    // === GET /api/sources/{id}/params ===
    @Test
    void getParams_401() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID_PARAMS, "Insee")
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void getParams_403() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID_PARAMS, "SOURCE1")
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test
    void getParams_200() throws Exception {
        mockMvc.perform(get(UrlConstants.API_SOURCES_ID_PARAMS, "SOURCE1")
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isOk());
    }

    // === PUT /api/sources/{id}/params ===
    @Test void putParams_401() throws Exception {
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID_PARAMS, "SOURCE1")
                        .content(createJsonParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
    @Test void putParams_403() throws Exception {
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID_PARAMS, "SOURCE1")
                        .content(createJsonParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole("PORTAL")))
                .andExpect(status().isForbidden());
    }
    @Test void putParams_200() throws Exception {
        mockMvc.perform(put(UrlConstants.API_SOURCES_ID_PARAMS, "SOURCE1")
                        .content(createJsonParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtWithRole("ADMIN")))
                .andExpect(status().isOk());
    }

    private String createJsonSource() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", "SOURCE1");
        jo.put("periodicity", PeriodicityEnum.X);
        return jo.toString();
    }

    private String createJsonParams() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("paramId", "SENSITIVITY");
        jo.put("paramValue", SensitivityEnum.NORMAL);
        return jo.toString();
    }

}


