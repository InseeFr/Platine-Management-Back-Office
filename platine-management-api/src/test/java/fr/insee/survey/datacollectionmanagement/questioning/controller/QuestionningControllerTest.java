package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactEventService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    MockMvc mockMvc;

    @Autowired
    QuestioningAccreditationRepository questioningAccreditationRepository;

    @Autowired
    ContactEventService contactEventService;

    @Autowired
    ContactSourceService contactSourceService;

    @Autowired
    PartitioningService partitioningService;

    @Autowired
    ViewService viewService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUserWithPermissions("test", AuthorityRoleEnum.ADMIN));
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
    @DisplayName("Assign an interrogation to a main contact that is already assigned to this interrogation")
    void updateInterrogation_ToMainContactAsMain_ok_AlreadyAssigned() throws Exception {
        UUID questioningId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");
        String contactId = "CONT1";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isOk());

        Optional<QuestioningAccreditation> qa = questioningAccreditationRepository.findAccreditationsByQuestioningIdAndIsMainTrue(questioningId);
        assertThat(qa).isPresent();
        assertThat(qa.get().isMain()).isTrue();
        assertThat(qa.get().getIdContact()).isEqualTo(contactId);
        assertThat(qa.get().getQuestioning().getId()).isEqualTo(questioningId);

        Questioning questioning = questioningService.findById(questioningId);
        Campaign campaign = partitioningService.findById(questioning.getIdPartitioning()).getCampaign();
        Source source =  campaign.getSurvey().getSource();
        ContactSource contactSource = contactSourceService.findContactSource(contactId, source.getId(), questioning.getSurveyUnit().getIdSu());

        assertThat(contactSource.getId().getContactId()).isEqualTo(contactId);
        assertThat(contactSource.getId().getSourceId()).isEqualTo(source.getId());
        assertThat(contactSource.getId().getSurveyUnitId()).isEqualTo(questioning.getSurveyUnit().getIdSu());
    }

    @Test
    @DisplayName("Assign an interrogation to a main contact")
    void updateInterrogation_ToMainContactAsMain_ok() throws Exception {
        UUID questioningId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");
        String contactId = "CONT2";
        String replacedContactId = "CONT1";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isOk());

        Optional<QuestioningAccreditation> qa = questioningAccreditationRepository.findAccreditationsByQuestioningIdAndIsMainTrue(questioningId);
        assertThat(qa).isPresent();
        assertThat(qa.get().isMain()).isTrue();
        assertThat(qa.get().getIdContact()).isEqualTo(contactId);
        assertThat(qa.get().getQuestioning().getId()).isEqualTo(questioningId);

        assertThat(contactEventService.findContactEventsByContactId(contactId)).hasSize(2);
        assertThat(contactEventService.findContactEventsByContactId(contactId).getLast().getType()).isEqualTo(ContactEventTypeEnum.update.toString());
        assertThat(contactEventService.findContactEventsByContactId(replacedContactId)).hasSize(2);
        assertThat(contactEventService.findContactEventsByContactId(replacedContactId).getLast().getType()).isEqualTo(ContactEventTypeEnum.update.toString());

        Questioning questioning = questioningService.findById(questioningId);
        Campaign campaign = partitioningService.findById(questioning.getIdPartitioning()).getCampaign();
        Source source =  campaign.getSurvey().getSource();
        SurveyUnit su = questioning.getSurveyUnit();
        String sourceId =  source.getId();
        String suId =  su.getIdSu();
        ContactSource contactSource = contactSourceService.findContactSource(contactId, sourceId, suId);

        assertThatThrownBy(() -> contactSourceService.findContactSource(replacedContactId, sourceId, suId))
                .isInstanceOf(NotFoundException.class);

        assertThat(contactSource.getId().getContactId()).isEqualTo(contactId);
        assertThat(contactSource.getId().getSourceId()).isEqualTo(source.getId());
        assertThat(contactSource.getId().getSurveyUnitId()).isEqualTo(questioning.getSurveyUnit().getIdSu());

        assertThat(contactSource.getId().getContactId()).isEqualTo(contactId);
        assertThat(contactSource.getId().getSourceId()).isEqualTo(source.getId());
        assertThat(contactSource.getId().getSurveyUnitId()).isEqualTo(questioning.getSurveyUnit().getIdSu());

        List<View> views = viewService.findByIdentifierAndIdSuAndCampaignId(contactId, su.getIdSu(), campaign.getId());
        assertThat(views).hasSize(1);
        View view = views.getFirst();

        assertThat(view.getIdSu()).isEqualTo(su.getIdSu());
        assertThat(view.getCampaignId()).isEqualTo(campaign.getId());
        assertThat(view.getIdentifier()).isEqualTo(contactId);

        List<View> viewsReplacedContact = viewService.findByIdentifierAndIdSuAndCampaignId(replacedContactId, su.getIdSu(), campaign.getId());
        assertThat(viewsReplacedContact).isEmpty();
    }

    @Test
    void updateInterrogation_ToMainContactAsMain_notFound() throws Exception {
        UUID questioningId = UUID.randomUUID();
        String contactId = "UNKNOWN";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateInterrogation_ToMainContactAsInterrogation_notFound() throws Exception {
        UUID questioningId = UUID.randomUUID();
        String contactId = "CONT1";

        mockMvc.perform(put(UrlConstants.API_MAIN_CONTACT_INTERROGATIONS_ASSIGN, questioningId, contactId)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePriorities_shouldReturn200_whenValidationOk() throws Exception {
        Questioning questioning = questioningService.findBySurveyUnitIdSu("100000001").stream().findFirst().get();
        UUID questioningId = questioning.getId();
        String json = """
                [
                  {
                    "interrogationId": "%s",
                    "priority": 1
                  }
                ]
                """.formatted(questioningId);

        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_PRIORITIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updatePriorities_shouldReturn400_whenValidationNotOk() throws Exception {
        String json = """
                [
                  {
                    "interrogationId": "11111111-1111-1111-1111-111111111111",
                    "priority": 1
                  }
                ]
                """;

        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_PRIORITIES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

}
