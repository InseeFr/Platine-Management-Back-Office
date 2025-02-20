package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

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
        questioningService = new QuestioningServiceImpl(
                questioningRepository, surveyUnitService, campaignService, partitioningService,
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
        SurveyUnit su = new SurveyUnit();
        su.setIdSu(SURVEY_UNIT_ID);
        questioning.setSurveyUnit(su);
        questioning.setModelName("MODEL");

        return questioning;
    }


}