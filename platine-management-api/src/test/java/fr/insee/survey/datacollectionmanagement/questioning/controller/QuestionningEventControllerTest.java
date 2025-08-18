package fr.insee.survey.datacollectionmanagement.questioning.controller;


import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ExpertEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class QuestionningEventControllerTest {

    @Autowired
    QuestioningService questioningService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    QuestioningEventService questioningEventService;

    @BeforeEach
    void init() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(
                    AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN)
            );
        }
    }


    @Test
    @Transactional
    void getQuestioningEventOk() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        String json = createJsonQuestioningEvent();
        this.mockMvc.perform(get(UrlConstants.API_QUESTIONING_ID_QUESTIONING_EVENTS, questioning.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json, JsonCompareMode.LENIENT));
    }

    @Test
    void getQuestioningEventNotFound() throws Exception {
        UUID identifier = UUID.randomUUID();
        this.mockMvc.perform(get(UrlConstants.API_QUESTIONING_ID_QUESTIONING_EVENTS, identifier)).andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    }

    @Test
    void createNotValidQuestioningEvent() throws Exception {
        String notValidEvent = "notValidEvent";
        UUID randomUUID = UUID.randomUUID();
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_TYPE, notValidEvent)
                        .contentType(MediaType.APPLICATION_JSON).content(createJsonQuestioningEventInputDto(randomUUID)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Type missing or not recognized. Only VALINT, VALPAP, REFUSAL, WASTE, HC, INITLA, PARTIELINT, PND are valid"));
    }

    @Test
    @Transactional
    void createValidQuestioningEvent() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        UUID id = questioning.getId();
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_TYPE, TypeQuestioningEvent.REFUSAL.name())
                        .contentType(MediaType.APPLICATION_JSON).content(createJsonQuestioningEventInputDto(id)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    private String createJsonQuestioningEvent() throws JSONException {
        JSONObject joEventInitla = new JSONObject();
        joEventInitla.put("type", "INITLA");

        JSONObject joPartiel = new JSONObject();
        joPartiel.put("type", "PARTIELINT");

        JSONObject joValint = new JSONObject();
        joValint.put("type", "VALINT");

        JSONArray ja = new JSONArray();
        ja.put(joEventInitla);
        ja.put(joPartiel);
        ja.put(joValint);

        System.out.println(ja.toString());
        return ja.toString();
    }

    private String createJsonQuestioningEventInputDto(UUID id) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("questioningId", id);

        return jo.toString();
    }

    @Test
    @DisplayName("Should record scores when record expertise event")
    void recordExpertiseEvent() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000005").stream().findFirst().get();
        assertThat(questioning.getScore()).isNull();
        assertThat(questioning.getScoreInit()).isNull();
        ExpertEventDto expertEventDto = new ExpertEventDto(4,4, TypeQuestioningEvent.EXPERT);
        String json = createJsonExpertEvent(expertEventDto);
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_ID_EXPERT_EVENTS, questioning.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        questioning = questioningService.findBySurveyUnitIdSu("100000005").stream().findFirst().get();
        assertThat(questioning.getScore()).isEqualTo(4);
        assertThat(questioning.getScoreInit()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should return bad request if type is null")
    void badRequestExpertiseEvent() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000005").stream().findFirst().get();
        ExpertEventDto expertEventDto = new ExpertEventDto(4,4, null);
        String json = createJsonExpertEvent(expertEventDto);
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_ID_EXPERT_EVENTS, questioning.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request if type is not expert_event")
    void badRequestExpertiseEvent2() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000005").stream().findFirst().get();
        ExpertEventDto expertEventDto = new ExpertEventDto(4,4, TypeQuestioningEvent.HC);
        String json = createJsonExpertEvent(expertEventDto);
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_ID_EXPERT_EVENTS, questioning.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not fount exception")
    void notFoundQuestioning() throws Exception {
        ExpertEventDto expertEventDto = new ExpertEventDto(4,4, TypeQuestioningEvent.EXPERT);
        String json = createJsonExpertEvent(expertEventDto);
        this.mockMvc.perform(post(UrlConstants.API_QUESTIONING_ID_EXPERT_EVENTS, UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete questioning event")
    @WithMockUser(roles={"ADMIN"})
    void shouldDeleteQuestioningEvent() throws Exception {
        assertThat(questioningEventService.findbyId(1L)).isNotNull();
        mockMvc.perform(delete(UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThatThrownBy(() -> questioningEventService.findbyId(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Should not delete questioning event")
    @WithMockUser(roles={"RESPONDENT"})
    void shouldNotDeleteQuestioningEvent() throws Exception {
        assertThat(questioningEventService.findbyId(1L)).isNotNull();
        mockMvc.perform(delete(UrlConstants.API_QUESTIONING_QUESTIONING_EVENTS_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        assertThat(questioningEventService.findbyId(1L)).isNotNull();
    }


    private String createJsonExpertEvent(ExpertEventDto event) throws JSONException {
        JSONObject joEvent = new JSONObject();
        joEvent.put("score", event.score());
        joEvent.put("score-init", event.scoreInit());
        joEvent.put("type", event.type());
        return joEvent.toString();
    }

}
