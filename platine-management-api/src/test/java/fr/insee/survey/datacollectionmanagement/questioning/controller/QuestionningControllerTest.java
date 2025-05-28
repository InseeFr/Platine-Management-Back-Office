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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("Assign an interrogation to a main contact that is already assigned to this interrogation")
    void updateInterrogation_ToMainContactAsMain_ok_AlreadyAssigned() throws Exception {
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
        Long questioningId = 1L;
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

        Optional<View> view = viewService.findByIdentifierAndIdSuAndCampaignId(contactId, su.getIdSu(), campaign.getId());

        assertThat(view).isPresent();
        assertThat(view.get().getIdSu()).isEqualTo(su.getIdSu());
        assertThat(view.get().getCampaignId()).isEqualTo(campaign.getId());
        assertThat(view.get().getIdentifier()).isEqualTo(contactId);

        Optional<View> viewReplacedContact = viewService.findByIdentifierAndIdSuAndCampaignId(replacedContactId, su.getIdSu(), campaign.getId());
        assertThat(viewReplacedContact).isNotPresent();
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
