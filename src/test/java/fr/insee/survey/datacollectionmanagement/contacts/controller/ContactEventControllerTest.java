package fr.insee.survey.datacollectionmanagement.contacts.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ContactEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getContactEventOk() throws Exception {
        String identifier = "CONT1";
        String json = createJsonContactEvent(identifier);
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID_CONTACTEVENTS, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getContactEventNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(Constants.API_CONTACTS_ID_CONTACTEVENTS, identifier)).andDo(print())
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

}
