package fr.insee.survey.datacollectionmanagement.user.controller.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.user.domain.UserEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class UserEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserEventOk() throws Exception {
        String identifier = "USER1";

        String json = createJsonUserEvent(identifier, null);
        this.mockMvc.perform(get(Constants.API_USERS_ID_USEREVENTS, identifier)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getUserEventNotFound() throws Exception {
        String identifier = "CONT500";
        this.mockMvc.perform(get(Constants.API_USERS_ID_USEREVENTS, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    private String createJsonUserEvent(String identifier, JSONObject payload) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONObject joPayload = new JSONObject();
        joPayload.put("identifier", identifier);
        joPayload.put("type", UserEvent.UserEventType.CREATE.name());
        jo.put("payload", payload);
        JSONArray ja = new JSONArray();
        ja.put(jo);
        return ja.toString();
    }

}
