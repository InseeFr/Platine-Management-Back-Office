package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CheckHabilitationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CheckHabilitationService checkHabilitationService;

    private final UUID questioningId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");

    @Test
    void shouldAllowAccessForAdmin() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("ADMIN", AuthorityRoleEnum.ADMIN));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldAllowAccessForRespondent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("CONT1", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldNotAllowAccessForRespondent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("NOTHAB", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

    @Test
    void shouldAllowAccessForAdminV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("ADMIN", AuthorityRoleEnum.ADMIN));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "id")
                        .param("campaign", "campaign")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldAllowAccessForRespondentV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("CONT1", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "100000000")
                        .param("campaign", "SOURCE12023T01")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldNotAllowAccessForRespondentV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("NOTHAB", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "100000000")
                        .param("campaign", "SOURCE12023T01")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

}