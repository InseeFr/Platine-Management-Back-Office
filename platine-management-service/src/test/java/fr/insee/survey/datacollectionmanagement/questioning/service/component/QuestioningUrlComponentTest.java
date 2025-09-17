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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private QuestioningUrlContext createQuestioningUrlContext(
            DataCollectionEnum dataCollection,
            String operation,
            boolean isBusiness, String surveyUnitLabel, String identificationName) {
        return new QuestioningUrlContext(
                surveyUnitId,
                questioningId,
                isBusiness,
                surveyUnitLabel,
                identificationName,
                String.format("%s-%s-%s",sourceId.toLowerCase(),surveyYear,period),
                dataCollection,
                sourceId.toLowerCase(),
                surveyYear,
                period.name(),
                operation,
                contactId);
    }

    private QuestioningUrlContext createQuestioningUrlContext(DataCollectionEnum dataCollection, String operation) {
        return createQuestioningUrlContext(dataCollection, operation, false, null, null);
    }

    private Partitioning mockPartitioning(DataCollectionEnum target) {
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);

        Survey survey = mock(Survey.class);
        when(survey.getSource()).thenReturn(source);
        when(survey.getYear()).thenReturn(surveyYear);


        Campaign campaign = mock(Campaign.class);
        when(campaign.getSurvey()).thenReturn(survey);
        when(campaign.getPeriodCollect()).thenReturn(PeriodEnum.T04);
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

    private static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return Arrays.stream(query.split("&"))
                .map(kv -> kv.split("=", 2))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv.length > 1 ? kv[1] : ""
                ));
    }

    private static String base64UrlDecode(String value) {
        if (value == null) return null;
        byte[] bytes = Base64.getUrlDecoder().decode(value);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    void testLunaticNormalInterviewer() {
        // given
        QuestioningUrlContext ctx = createQuestioningUrlContext(DataCollectionEnum.LUNATIC_NORMAL, null);

        // when
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, ctx);

        // then
        URI uri = URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("lunatic-normal");
        assertThat(uri.getPath()).isEqualTo("/v3/interrogations/" + questioningId);

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsKey("pathAssistance");

        String assistance = base64UrlDecode(q.get("pathAssistance"));
        assertThat(assistance).contains("/mes-enquetes/" + sourceId.toLowerCase())
                .contains("interrogationId=" + questioningId)
                .contains("surveyUnitId=" + surveyUnitId)
                .contains("contactId=" + contactId);
    }

    @Test
    void testLunaticSensitiveInterviewer() {
        // given
        QuestioningUrlContext ctx = createQuestioningUrlContext(DataCollectionEnum.LUNATIC_SENSITIVE, null);

        // when
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, ctx);

        // then
        URI uri = URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("lunatic-sensitive");
        assertThat(uri.getPath()).isEqualTo("/v3/interrogations/" + questioningId);

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsKey("pathAssistance");

        String assistance = base64UrlDecode(q.get("pathAssistance"));
        assertThat(assistance).contains("/mes-enquetes/" + sourceId.toLowerCase())
                .contains("interrogationId=" + questioningId)
                .contains("surveyUnitId=" + surveyUnitId)
                .contains("contactId=" + contactId);
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
        String expected = "https://xform1/repondre/sourceid-2024-T04/" + surveyUnitId;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer() {
        QuestioningUrlContext questioningUrlContext = createQuestioningUrlContext(DataCollectionEnum.XFORM2, null);
        String url = component.buildAccessUrl(UserRoles.REVIEWER, questioningUrlContext);
        String expected = "https://xform2/visualiser/sourceid-2024-T04/" + surveyUnitId;
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
        // when
        String url = component.getAccessUrl(
                UserRoles.INTERVIEWER,
                createQuestioning(),
                mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL)
        );

        // then
        URI uri = URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("lunatic-normal");
        assertThat(uri.getPath()).isEqualTo("/v3/interrogations/" + questioningId);

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsKey("pathAssistance");

        String assistance = base64UrlDecode(q.get("pathAssistance"));
        assertThat(assistance).contains("/mes-enquetes/" + sourceId.toLowerCase())
            .contains("interrogationId=" + questioningId)
            .contains("surveyUnitId=" + surveyUnitId)
            .contains("contactId=");
    }

    @Test
    void testLunaticNormalInterviewer_AccessUrl_with_SU_Info() {
        // given
        QuestioningUrlContext ctx = createQuestioningUrlContext(
                DataCollectionEnum.LUNATIC_NORMAL, null, true, "Entreprise", "Test&Recette?"
        );
        String expectedLabel = "Entreprise Test&Recette? (" + surveyUnitId + ")";

        // when
        String url = component.buildAccessUrl(UserRoles.INTERVIEWER, ctx);

        // then
        URI uri = URI.create(url);
        assertThat(uri.getHost()).isEqualTo("lunatic-normal");

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsKeys("pathAssistance", "surveyUnitLabel");

        String labelDecoded = base64UrlDecode(q.get("surveyUnitLabel"));
        assertThat(labelDecoded).isEqualTo(expectedLabel);
    }

    @Test
    void testLunaticNormalReviewer_AccessUrl_with_SU_Info() {
        // given
        QuestioningUrlContext ctx = createQuestioningUrlContext(
                DataCollectionEnum.LUNATIC_NORMAL, null, true, null, "Test"
        );

        String expectedLabel = component.buildSurveyUnitLabelDetails(null, "Test", surveyUnitId);

        // when
        String url = component.buildAccessUrl(UserRoles.REVIEWER, ctx);

        // then
        URI uri = URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("lunatic-normal");
        assertThat(uri.getPath()).isEqualTo("/v3/review/interrogations/" + questioningId);

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsKey("surveyUnitLabel");

        String labelDecoded = base64UrlDecode(q.get("surveyUnitLabel"));
        assertThat(labelDecoded).isEqualTo(expectedLabel);
    }

    @Test
    void testLunaticSensitiveInterviewer_defaultAccessUrl() {
        // when
        String url = component.getAccessUrl(
                UserRoles.INTERVIEWER,
                createQuestioning(),
                mockPartitioning(DataCollectionEnum.LUNATIC_SENSITIVE)
        );

        // then
        URI uri = URI.create(url);
        assertThat(uri.getScheme()).isEqualTo("https");
        assertThat(uri.getHost()).isEqualTo("lunatic-sensitive");
        assertThat(uri.getPath()).isEqualTo("/v3/interrogations/" + questioningId);

        Map<String, String> q = parseQuery(uri.getQuery());
        assertThat(q).containsOnlyKeys("pathAssistance");

        String assistance = base64UrlDecode(q.get("pathAssistance"));
        assertThat(assistance)
                .startsWith("/mes-enquetes/" + sourceId.toLowerCase() + "/contacter-assistance/auth")
                .contains("interrogationId=" + questioningId)
                .contains("surveyUnitId=" + surveyUnitId)
                .contains("contactId=");
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
        String expected = "https://xform1/repondre/sourceid-2024-T04/" + surveyUnitId;
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, createQuestioning(), mockPartitioning(DataCollectionEnum.XFORM2));
        String expected = "https://xform2/visualiser/sourceid-2024-T04/" + surveyUnitId;
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
        String url = component.buildDepositProofUrl(questioningId, DataCollectionEnum.LUNATIC_NORMAL);

        String expected = questionnaireApiUrl
                + "/api/interrogations/" + questioningId + "/deposit-proof";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testDepositProofUrl_lunaticSensitive() {
        String url = component.buildDepositProofUrl(questioningId, DataCollectionEnum.LUNATIC_SENSITIVE);

        String expected = questionnaireApiSensitiveUrl
                + "/api/interrogations/" + questioningId + "/deposit-proof";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testDepositProofUrl_otherDataCollection_returnsNull() {
        String url = component.buildDepositProofUrl(questioningId, DataCollectionEnum.XFORM1);

        assertThat(url).isNull();
    }

    @Test
    void testDownloadUrl_ofats() {
        QuestioningUrlContext ctx =
                createQuestioningUrlContext(DataCollectionEnum.FILE_UPLOAD, "ofats");

        // when
        String url = component.buildDownloadUrl(ctx);

        // then
        assertThat(url).isEqualTo("insee-" + surveyUnitId + "-ofats-2024.xlsx");
    }

    @Test
    void testDownloadUrl_default() {
        QuestioningUrlContext ctx =
                createQuestioningUrlContext(DataCollectionEnum.FILE_UPLOAD, "test");

        // when
        String url = component.buildDownloadUrl(ctx);

        // then
        assertThat(url).isEqualTo("test-" + surveyUnitId + "-sourceid-2024-T04.xlsx");
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = { "   " })
    void buildSurveyUnitLabelDetails_returnsIdentificationWhenLabelBlankish(String label) {
        String identificationName = "Alpha";

        String result = component.buildSurveyUnitLabelDetails(label, identificationName, surveyUnitId);

        assertThat(result).isEqualTo(identificationName + " (" + surveyUnitId + ")");
    }

    @Test
    void buildSurveyUnitLabelDetails_capitalizesFirstLetter_only() {
        String identificationName = "Alpha";

        String result = component.buildSurveyUnitLabelDetails("entreprise", identificationName, surveyUnitId);

        assertThat(result).isEqualTo("Entreprise " + identificationName + " (" + surveyUnitId + ")");
    }
}
