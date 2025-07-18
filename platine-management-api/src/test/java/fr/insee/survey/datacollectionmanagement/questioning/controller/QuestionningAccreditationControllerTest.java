package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import jakarta.transaction.Transactional;
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

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
class QuestionningAccreditationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    QuestioningService questioningService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void getQuestioningAccreditationOk() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        String json = createJsonQuestioningAcreditation();
        this.mockMvc.perform(get(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, questioning.getId())).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(json, false));
    }

    @Test
    void getQuestioningAccreditationNotFound() throws Exception {
        UUID identifier = UUID.randomUUID();
        this.mockMvc.perform(get(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void postAccreditationQuestioningNotFound() throws Exception {
        UUID idQuestioning = UUID.randomUUID();
        String idContact = "CONT1";

        // create contact - status created
        QuestioningAccreditation accreditation = initAccreditation(idContact);
        String jsonAccreditation = createJson(accreditation);
        mockMvc.perform(
                        post(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, idQuestioning)
                                .content(jsonAccreditation).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void postAccreditationContactNotFound() throws Exception {
        Questioning q = questioningService.findByIdPartitioning("SOURCE12023T1000").stream().findFirst().get();
        UUID idQuestioning = q.getId();
        String idContact = "CONT7500";

        // create contact - status created
        QuestioningAccreditation accreditation = initAccreditation(idContact);
        String jsonAccreditation = createJson(accreditation);
        mockMvc.perform(
                        post(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, idQuestioning)
                                .content(jsonAccreditation).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void postAccreditationCreateUpdate() throws Exception {
        Questioning q = questioningService.findByIdPartitioning("SOURCE12023T1000").stream().findFirst().get();
        UUID idQuestioning = q.getId();
        String idContact = "CONT5";

        // create accreditation - status created
        QuestioningAccreditation accreditation = initAccreditation(idContact);
        String jsonAccreditation = createJson(accreditation);
        mockMvc.perform(
                        post(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, idQuestioning)
                                .content(jsonAccreditation).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAccreditation, false));
        Questioning questioning = questioningService.findById(idQuestioning);
        Set<QuestioningAccreditation> setAccreditationFound = questioning.getQuestioningAccreditations();
        QuestioningAccreditation accreditationFound = setAccreditationFound.stream()
                .filter(acc -> acc.getIdContact().equals(idContact))
                .toList().getFirst();
        assertEquals(accreditationFound.getCreationAuthor(), accreditation.getCreationAuthor());
        assertEquals(accreditationFound.getIdContact(), accreditation.getIdContact());

        // update accreditation - status ok
        accreditation.setMain(true);
        String jsonAccreditationUpdate = createJson(accreditation);
        mockMvc.perform(
                        post(UrlConstants.API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS, idQuestioning)
                                .content(jsonAccreditationUpdate).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonAccreditationUpdate, false));

        QuestioningAccreditation accreditationFoundAfterUpdate = questioningService.findById(idQuestioning)
                .getQuestioningAccreditations().stream().filter(acc -> acc.getIdContact().equals(idContact))
                .toList().getFirst();
        assertTrue(accreditationFoundAfterUpdate.isMain());

    }

    private QuestioningAccreditation initAccreditation(String idContact) {
        QuestioningAccreditation questionnAccreditation = new QuestioningAccreditation();
        questionnAccreditation.setIdContact(idContact);
        questionnAccreditation.setMain(false);
        questionnAccreditation.setCreationAuthor("ME");
        return questionnAccreditation;
    }

    private String createJson(QuestioningAccreditation accreditation) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("creationAuthor", accreditation.getCreationAuthor());
        jo.put("idContact", accreditation.getIdContact());
        jo.put("main", accreditation.isMain());
        return jo.toString();
    }

    private String createJsonQuestioningAcreditation() throws JSONException {
        JSONObject jo1 = new JSONObject();
        jo1.put("idContact", "CONT1");

        JSONObject jo2 = new JSONObject();
        jo2.put("idContact", "CONT2");

        JSONObject jo3 = new JSONObject();
        jo3.put("idContact", "CONT3");

        JSONObject jo4 = new JSONObject();
        jo4.put("idContact", "CONT4");

        JSONArray ja = new JSONArray();
        ja.put(jo1);
        ja.put(jo2);
        ja.put(jo3);
        ja.put(jo4);

        return ja.toString();
    }

}
