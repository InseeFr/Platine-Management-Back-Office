package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class QuestionningControllerTest {

    @Autowired
    QuestioningService questioningService;
    @Autowired
    ContactService contactService;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    QuestioningAccreditationRepository questioningAccreditationRepository;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }


    @Test
    void getQuestioningsBySurveyUnit() throws Exception {
        String idSu = "100000000";
        String json = createJsonQuestionings(idSu);
        this.mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID_QUESTIONINGS, idSu)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json, false));

    }


    private String createJsonQuestionings(String id) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("surveyUnitId", id);
        JSONArray ja = new JSONArray();
        ja.put(jo);
        System.out.println(ja);
        return ja.toString();
    }

    @Test
    void updateInterrogation_ToMainContactAsMain_ok() throws Exception {
        Long questioningId = 1L;
        String contactId = "CONT1";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isOk());

        Optional<QuestioningAccreditation> qa = questioningAccreditationRepository.findAccreditationsByQuestioningIdAndIsMainTrue(questioningId);
        assertThat(qa).isPresent();
        assertThat(qa.get().isMain()).isTrue();
        assertThat(qa.get().getIdContact()).isEqualTo(contactId);
        assertThat(qa.get().getQuestioning().getId()).isEqualTo(questioningId);
    }

    @Test
    void updateInterrogation_ToMainContactAsMain_notFound() throws Exception {
        Long questioningId = 999L;
        String contactId = "UNKNOWN";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateInterrogation_ToMainContactAsInterrogation_notFound() throws Exception {
        Long questioningId = 999L;
        String contactId = "CONT1";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isNotFound());
    }

}
