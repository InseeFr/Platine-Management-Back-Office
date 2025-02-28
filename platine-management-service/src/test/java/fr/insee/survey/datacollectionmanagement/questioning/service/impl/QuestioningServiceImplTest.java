package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.TooManyValuesException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestioningServiceImplTest {


    private static final String SURVEY_UNIT_ID = "12345";
    private static final String QUESTIONING_NORMAL_URL = "http://questioning.com/normal";
    private static final String QUESTIONING_SENSITIVE_URL = "http://questioning.com/sensitive";
    private static final String QUESTIONING_XFORMS1 = "http://questioning.com/xforms1";
    private static final String QUESTIONING_XFORMS2 = "http://questioning.com/xforms2";

    @Mock
    private QuestioningRepository questioningRepository;

    @Mock
    private SurveyUnitService surveyUnitService;

    @Mock
    private PartitioningService partitioningService;

    @Mock
    private QuestioningEventService questioningEventService;

    @Mock
    private QuestioningAccreditationService questioningAccreditationService;

    @Mock
    private QuestioningCommunicationService questioningCommunicationService;

    @Mock
    private QuestioningCommentService questioningCommentService;

    @Mock
    private CampaignService campaignService;

    @Mock
    private ModelMapper modelMapper;

    private Partitioning part = initPartitioning();

    private Questioning questioning = initQuestioning();

    private QuestioningServiceImpl questioningService;

    private Partitioning partitioning;

    @Test
    @DisplayName("Check the V1 url in interviewer mode")
    void getV1UrlInterviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.INTERVIEWER;
        String modelName = "m1";
        String surveyUnitId = "999999999";
        String url = questioningService.buildXformUrl(baseUrl, role, modelName, surveyUnitId);
        String expected = "https://urlBase/repondre/m1/999999999";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check the V1 url in reviewer mode")
    void getV1UrlReviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.REVIEWER;
        String modelName = "m1";
        String surveyUnitId = "999999999";
        String url = questioningService.buildXformUrl(baseUrl, role, modelName, surveyUnitId);
        String expected = "https://urlBase/visualiser/m1/999999999";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check the V3 url in interviewer mode")
    void getV3UrlInterviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.INTERVIEWER;
        String modelName = "model";
        String surveyUnitId = "999999999";
        String sourceId = "enq";
        Long questioningId = 123456789L;
        String url = questioningService.buildLunaticUrl(baseUrl, role, modelName, surveyUnitId, sourceId, questioningId);
        String expected = "https://urlBase/v3/questionnaire/model/unite-enquetee/999999999?pathLogout=%2Fenq&pathAssistance=%2Fenq%2Fcontacter-assistance%2Fauth%3FquestioningId%3D123456789";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check the V3 url in reviewer mode")
    void getV3UrlReviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.REVIEWER;
        String modelName = "model";
        String surveyUnitId = "999999999";
        String sourceId = "enq";
        Long questioningId = 123456789L;
        String url = questioningService.buildLunaticUrl(baseUrl, role, modelName, surveyUnitId, sourceId, questioningId);
        String expected = "https://urlBase/v3/review/questionnaire/model/unite-enquetee/999999999";
        assertThat(url).isEqualTo(expected);
    }


    @BeforeEach
    void setUp() {

        partitioning = new Partitioning();


        questioningService = new QuestioningServiceImpl(
                questioningRepository, surveyUnitService, partitioningService,
                questioningEventService, questioningAccreditationService,
                questioningCommunicationService, questioningCommentService,
                modelMapper, QUESTIONING_NORMAL_URL, QUESTIONING_SENSITIVE_URL,
                QUESTIONING_XFORMS1, QUESTIONING_XFORMS2);

    }

    @Test
    void testGetAccessUrl_V1() {
        Campaign campaign = part.getCampaign();
        campaign.setDataCollectionTarget(DataCollectionEnum.XFORM1);
        part.setCampaign(campaign);

        String result = questioningService.getAccessUrl(UserRoles.REVIEWER, questioning, part);

        assertThat(result).isNotNull().contains(QUESTIONING_XFORMS1);
    }

    @Test
    void testGetAccessUrl_V3_Sensitive() {
        Campaign campaign = part.getCampaign();
        campaign.setDataCollectionTarget(DataCollectionEnum.LUNATIC_SENSITIVE);
        part.setCampaign(campaign);
        String result = questioningService.getAccessUrl(UserRoles.REVIEWER, questioning, part);

        assertThat(result).isNotNull().contains(QUESTIONING_SENSITIVE_URL);
    }

    @Test
    void testGetAccessUrl_V3_NonSensitive() {
        String result = questioningService.getAccessUrl(UserRoles.REVIEWER, questioning, part);

        assertThat(result).isNotNull().contains(QUESTIONING_NORMAL_URL);
    }

    @Test
    void testGetAccessUrl_Default() {

        String result = questioningService.getAccessUrl(UserRoles.REVIEWER, questioning, part);

        assertThat(result).isNotNull().contains("v3");
    }
    @Test
    @DisplayName("Check notFoundException when 0 questioning found for 1 surveyUnit and one camapaign")
    void findByCampaignIdAndSurveyUnitIdSuEmptyResult() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThatThrownBy(() -> questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No questioning found for campaignId");
    }

    @Test
    @DisplayName("Check TooManyValuesException when 2 questioning found for 1 surveyUnit and onr campaign")
    void findByCampaignIdAndSurveyUnitIdSuTwoResults() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();
        listQuestioning.add(initQuestioning());
        listQuestioning.add(initQuestioning());

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThatThrownBy(() -> questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId))
                .isInstanceOf(TooManyValuesException.class)
                .hasMessageContaining("2 questionings found for");
    }

    @Test
    @DisplayName("Check ok when 1 and only 1 questioning found for 1 surveyUnit and onr campaign")
    void findByCampaignIdAndSurveyUnitIdSuOneResult() {
        String campaignId = "CAMP2025X00";
        String surveyUnitId = "SURVEYUNITID";
        List<Questioning> listQuestioning = new ArrayList<>();
        Questioning q = initQuestioning();
        listQuestioning.add(q);

        when(questioningRepository.findQuestioningByCampaignIdAndSurveyUnitId(campaignId, surveyUnitId)).thenReturn(listQuestioning);

        assertThat(questioningService.findByCampaignIdAndSurveyUnitIdSu(campaignId, surveyUnitId).getQuestioningId()).isEqualTo(q.getId());
    }


    private Partitioning initPartitioning() {
        Source source = new Source();
        source.setId("SOURCEID");
        Survey survey = new Survey();
        survey.setId("SURVEYID");
        survey.setSource(source);
        Campaign campaign = new Campaign();
        campaign.setId("CAMPAIGNID");
        campaign.setSurvey(survey);
        part = new Partitioning();
        part.setId("PARTITIONINGID");
        part.setCampaign(campaign);
        return part;
    }

    private Questioning initQuestioning() {
        questioning = new Questioning();
        questioning.setId(1L);
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(SURVEY_UNIT_ID);
        questioning.setSurveyUnit(su);
        questioning.setModelName("MODEL");

        return questioning;
    }

    @DisplayName("Should return INCOMING when today is before opening date")
    @Test
    void getQuestioningStatusTest() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.INCOMING);
    }

    @DisplayName("Should return NOT_RECEIVED when no events exist")
    @Test
    void getQuestioningStatusTest2() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        questioning.setQuestioningEvents(new HashSet<>());
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when refused event exists")
    @Test
    void getQuestioningStatusTest3() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.REFUSAL);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return RECEIVED when validated event exists before closing date")
    @Test
    void getQuestioningStatusTest4() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS)).thenReturn(true);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.RECEIVED);
    }

    @DisplayName("Should return OPEN when opened event exists before closing date")
    @Test
    void getQuestioningStatusTest5() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.INITLA);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.VALIDATED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.OPENED_EVENTS)).thenReturn(true);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.OPEN);
    }

    @DisplayName("Should return NOT_RECEIVED when no valid event exists after closing date")
    @Test
    void getQuestioningStatusTest6() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        Set<QuestioningEvent> events = new HashSet<>();
        questioning.setQuestioningEvents(events);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid events exist after closing date")
    @Test
    void getQuestioningStatusTest7() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setType(TypeQuestioningEvent.VALINT);
        events.add(questioningEvent);
        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }

    @DisplayName("Should return NOT_RECEIVED when valid and refused events exist before closing date")
    @Test
    void getQuestioningStatusTest8() {
        partitioning.setOpeningDate(new Date(System.currentTimeMillis() - 96400000)); // Yesterday
        partitioning.setClosingDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        Set<QuestioningEvent> events = new HashSet<>();
        QuestioningEvent questioningEventValid = new QuestioningEvent();
        questioningEventValid.setType(TypeQuestioningEvent.VALINT);
        QuestioningEvent questioningEventRefused = new QuestioningEvent();
        questioningEventRefused.setType(TypeQuestioningEvent.HC);
        events.add(questioningEventValid);
        events.add(questioningEventRefused);

        questioning.setQuestioningEvents(events);

        when(questioningEventService.containsQuestioningEvents(questioning, TypeQuestioningEvent.REFUSED_EVENTS)).thenReturn(false);
        QuestionnaireStatusTypeEnum status = questioningService.getQuestioningStatus(questioning, partitioning);
        assertThat(status).isEqualTo(QuestionnaireStatusTypeEnum.NOT_RECEIVED);
    }
}