package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ContactEventControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ContactEventService contactEventService;

    @Autowired
    private ContactService contactService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getContactEventOk() throws Exception {
        String identifier = "CONT1";
        String json = createJsonContactEvent(identifier);
        String response = this.mockMvc.perform(get(UrlConstants.API_CONTACTS_ID_CONTACTEVENTS, identifier))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(json, response, JSONCompareMode.LENIENT);
    }

    @Test
    void getContactEventOkForSupportRole() throws Exception {
        String identifier = "CONT1";
        String json = createJsonContactEvent(identifier);
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.SUPPORT));
        String response = this.mockMvc
                .perform(get(UrlConstants.API_CONTACTS_ID_CONTACTEVENTS, identifier))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(json, response, JSONCompareMode.LENIENT);
    }

    @Test
    void getContactEventNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(UrlConstants.API_CONTACTS_ID_CONTACTEVENTS, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    private String createJsonContactEvent(String identifier) throws JSONException {
        //region Description
        JSONObject jo = new JSONObject();
        //endregion
        JSONObject joPayload = new JSONObject();
        joPayload.put("contact_identifier", identifier);
        jo.put("payload", joPayload);
        JSONArray ja = new JSONArray();
        ja.put(jo);
        return ja.toString();
    }

    @Test
    void getAllContactEventsOk() throws Exception {
        String contactId = "CONT1";

        this.mockMvc.perform(get(UrlConstants.API_CONTACT_CONTACTEVENTS)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser(contactId, AuthorityRoleEnum.RESPONDENT))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

  
}
