package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningUrlContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestioningUrlComponentTest {

    private QuestioningUrlComponent component;

    private final String lunaticNormalUrl = "https://lunatic-normal";
    private final String lunaticSensitiveUrl = "https://lunatic-sensitive";
    private final String questionnaireApiUrl = "https://questionnaire-api";
    private final String questionnaireApiSensitiveUrl = "https://questionnaire-api-sensitive";
    private final String xform1Url = "https://xform1";
    private final String xform2Url = "https://xform2";

    private final PeriodEnum period = PeriodEnum.T04;
    private final String surveyUnitId = "SURVEYID";
    private final UUID questioningId = UUID.randomUUID();
    private final String sourceId = "SOURCEID";
    private final String contactId = "TOTO";
    private final Integer surveyYear = 2024;

    @BeforeEach
    void setUp() {
        component = new QuestioningUrlComponent(lunaticNormalUrl, lunaticSensitiveUrl, questionnaireApiUrl, questionnaireApiSensitiveUrl, xform1Url, xform2Url);
    }

    private QuestioningUrlContext createQuestioningUrlContext(DataCollectionEnum dataCollection, String operation) {
        return new QuestioningUrlContext(
                surveyUnitId,
                questioningId,
                String.format("%s-%s-%s",sourceId.toLowerCase(),surveyYear,period),
                dataCollection,
                sourceId.toLowerCase(),
                surveyYear,
                period.name(),
                operation,
                contactId);
    }

    private Partitioning mockPartitioning(DataCollectionEnum target) {
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);

        Survey survey = mock(Survey.class);
        when(survey.getSource()).thenReturn(source);
        when(survey.getYear()).thenReturn(surveyYear);


        Campaign campaign = mock(Campaign.class);
        when(campaign.getSurvey()).thenReturn(survey);
        when(campaign.getPeriod()).thenReturn(PeriodEnum.T04);

        when(campaign.getDataCollectionTarget()).thenReturn(target);

        Partitioning partitioning = mock(Partitioning.class);
        partitioning.setCampaign(campaign);
        when(partitioning.getCampaign()).thenReturn(campaign);

        return partitioning;
    }

    private Questioning createQuestioning() {
        SurveyUnit unit = new SurveyUnit();
        unit.setIdSu(surveyUnitId);

        Questioning questioning = new Questioning();
        questioning.setSurveyUnit(unit);
        questioning.setId(questioningId);
        return questioning;
    }

    @Test
    void testLunaticNormalInterviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.LUNATIC_NORMAL, null);
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, questioningUrlContext);

        String expected = "https://lunatic-normal/v3/interrogations/" + questioningId  +
                "?pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FinterrogationId%3D" + questioningId + "%26surveyUnitId%3DSURVEYID%26contactId%3DTOTO";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticSensitiveInterviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.LUNATIC_SENSITIVE, null);
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, questioningUrlContext);

        String expected = "https://lunatic-sensitive/v3/interrogations/" + questioningId  +
                "?pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FinterrogationId%3D" + questioningId + "%26surveyUnitId%3DSURVEYID%26contactId%3DTOTO";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNormalReviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.LUNATIC_NORMAL, null);
        String url = component.buildAccessUrl(UserRoles.REVIEWER, questioningUrlContext);
        String expected = "https://lunatic-normal/v3/review/interrogations/" + questioningId ;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform1Interviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.XFORM1, null);
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, questioningUrlContext);
        String expected = "https://xform1/repondre/sourceid-2024-T04/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.XFORM2, null);
        String url = component.buildAccessUrl(UserRoles.REVIEWER, questioningUrlContext);
        String expected = "https://xform2/visualiser/sourceid-2024-T04/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testFallbackToLunaticNormalWhenTargetIsNull() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(null, null);
        String url = component.buildAccessUrl(UserRoles.REVIEWER, questioningUrlContext);
        String expected = "https://lunatic-normal/v3/review/interrogations/" + questioningId ;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testFileUploadAccessUrlEmpty() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.FILE_UPLOAD, null);
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, questioningUrlContext);
        assertThat(url).isEmpty();
    }

    @Test
    void testNullData_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, createQuestioning(), null);
        assertThat(url).isEmpty();
    }

    @Test
    void testNullData2_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, null, mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));
        assertThat(url).isEmpty();
    }

    @Test
    void testLunaticNormalInterviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));

        String expected = "https://lunatic-normal/v3/interrogations/" + questioningId  +
                "?pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FinterrogationId%3D" + questioningId + "%26surveyUnitId%3DSURVEYID%26contactId%3D";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticSensitiveInterviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_SENSITIVE));

        String expected = "https://lunatic-sensitive/v3/interrogations/" + questioningId  +
                "?pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FinterrogationId%3D" + questioningId + "%26surveyUnitId%3DSURVEYID%26contactId%3D";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNormalReviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));
        String expected = "https://lunatic-normal/v3/review/interrogations/" + questioningId;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform1Interviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.XFORM1));
        String expected = "https://xform1/repondre/sourceid-2024-T04/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.XFORM2));
        String expected = "https://xform2/visualiser/sourceid-2024-T04/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testFallbackToLunaticNormalWhenTargetIsNull_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, createQuestioning(), mockPartitioning(null));
        String expected = "https://lunatic-normal/v3/review/interrogations/" + questioningId;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNullRole_returnsEmptyString() {
        String url = component.getAccessUrl(null, createQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));
        assertThat(url).isEmpty();
    }

    @Test
    void testXformNullRole_returnsEmptyString() {
        String url = component.getAccessUrl(null, createQuestioning(), mockPartitioning(DataCollectionEnum.XFORM1));
        assertThat(url).isEmpty();
    }

    @Test
    void testDepositProofUrl_lunaticNormal() {
        String url = component.buildDepositProofUrl(surveyUnitId, DataCollectionEnum.LUNATIC_NORMAL);

        String expected = questionnaireApiUrl
                + "/api/survey-unit/" + surveyUnitId + "/deposit-proof";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testDepositProofUrl_lunaticSensitive() {
        String url = component.buildDepositProofUrl(surveyUnitId, DataCollectionEnum.LUNATIC_SENSITIVE);

        String expected = questionnaireApiSensitiveUrl
                + "/api/survey-unit/" + surveyUnitId + "/deposit-proof";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testDepositProofUrl_otherDataCollection_returnsNull() {
        String url = component.buildDepositProofUrl(surveyUnitId, DataCollectionEnum.XFORM1);

        assertThat(url).isNull();
    }

    @Test
    void testDownloadUrl_ofats() {
        QuestioningUrlContext ctx =
                createQuestioningUrlContext(DataCollectionEnum.FILE_UPLOAD, "ofats");

        // when
        String url = component.buildDownloadUrl(ctx);

        // then
        assertThat(url).isEqualTo("insee-SURVEYID-ofats-2024.xlsx");
    }

    @Test
    void testDownloadUrl_default() {
        QuestioningUrlContext ctx =
                createQuestioningUrlContext(DataCollectionEnum.FILE_UPLOAD, "test");

        // when
        String url = component.buildDownloadUrl(ctx);

        // then
        assertThat(url).isEqualTo("test-SURVEYID-sourceid-2024-T04.xlsx");
    }




}
