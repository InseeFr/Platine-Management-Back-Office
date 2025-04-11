package fr.insee.survey.datacollectionmanagement.questioning.service.component;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestioningUrlComponentTest {

    private QuestioningUrlComponent component;

    private final String lunaticNormalUrl = "https://lunatic-normal";
    private final String lunaticSensitiveUrl = "https://lunatic-sensitive";
    private final String xform1Url = "https://xform1";
    private final String xform2Url = "https://xform2";

    private final String modelName = "MODEL";
    private final String surveyUnitId = "SURVEYID";
    private final Long questioningId = 1L;
    private final String sourceId = "SOURCEID";
    private final String contactId = "TOTO";

    @BeforeEach
    void setUp() {
        component = new QuestioningUrlComponent(lunaticNormalUrl, lunaticSensitiveUrl, xform1Url, xform2Url);
    }

    private Partitioning mockPartitioning(DataCollectionEnum target) {
        Source source = mock(Source.class);
        when(source.getId()).thenReturn(sourceId);

        Survey survey = mock(Survey.class);
        when(survey.getSource()).thenReturn(source);

        Campaign campaign = mock(Campaign.class);
        when(campaign.getSurvey()).thenReturn(survey);
        when(campaign.getDataCollectionTarget()).thenReturn(target);

        Partitioning partitioning = mock(Partitioning.class);
        when(partitioning.getCampaign()).thenReturn(campaign);

        return partitioning;
    }

    private Questioning mockQuestioning() {
        SurveyUnit unit = mock(SurveyUnit.class);
        when(unit.getIdSu()).thenReturn(surveyUnitId);

        Questioning questioning = mock(Questioning.class);
        when(questioning.getSurveyUnit()).thenReturn(unit);
        when(questioning.getModelName()).thenReturn(modelName);
        when(questioning.getId()).thenReturn(questioningId);
        return questioning;
    }

    @Test
    void testLunaticNormalInterviewer() {
        String url = component.getAccessUrlWithContactId(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL), contactId);

        String expected = "https://lunatic-normal/v3/questionnaire/MODEL/unite-enquetee/SURVEYID" +
                "?pathLogout=%2Fdeconnexion" +
                "&pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FquestioningId%3D1%26surveyUnitId%3DSURVEYID%26contactId%3DTOTO";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticSensitiveInterviewer() {
        String url = component.getAccessUrlWithContactId(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_SENSITIVE), contactId);

        String expected = "https://lunatic-sensitive/v3/questionnaire/MODEL/unite-enquetee/SURVEYID" +
                "?pathLogout=%2Fdeconnexion" +
                "&pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FquestioningId%3D1%26surveyUnitId%3DSURVEYID%26contactId%3DTOTO";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNormalReviewer() {
        String url = component.getAccessUrlWithContactId(UserRoles.REVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL), contactId);
        String expected = "https://lunatic-normal/v3/review/questionnaire/MODEL/unite-enquetee/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform1Interviewer() {
        String url = component.getAccessUrlWithContactId(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.XFORM1), contactId);
        String expected = "https://xform1/repondre/MODEL/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer() {
        String url = component.getAccessUrlWithContactId(UserRoles.REVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.XFORM2), contactId);
        String expected = "https://xform2/visualiser/MODEL/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testFallbackToLunaticNormalWhenTargetIsNull() {
        Partitioning part = mockPartitioning(null);
        String url = component.getAccessUrlWithContactId(UserRoles.REVIEWER, mockQuestioning(), part, contactId);
        String expected = "https://lunatic-normal/v3/review/questionnaire/MODEL/unite-enquetee/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNormalInterviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));

        String expected = "https://lunatic-normal/v3/questionnaire/MODEL/unite-enquetee/SURVEYID" +
                "?pathLogout=%2Fdeconnexion" +
                "&pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FquestioningId%3D1%26surveyUnitId%3DSURVEYID%26contactId%3D";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticSensitiveInterviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_SENSITIVE));

        String expected = "https://lunatic-sensitive/v3/questionnaire/MODEL/unite-enquetee/SURVEYID" +
                "?pathLogout=%2Fdeconnexion" +
                "&pathAssistance=%2Fmes-enquetes%2Fsourceid%2Fcontacter-assistance%2Fauth%3FquestioningId%3D1%26surveyUnitId%3DSURVEYID%26contactId%3D";

        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNormalReviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));
        String expected = "https://lunatic-normal/v3/review/questionnaire/MODEL/unite-enquetee/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform1Interviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.INTERVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.XFORM1));
        String expected = "https://xform1/repondre/MODEL/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testXform2Reviewer_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, mockQuestioning(), mockPartitioning(DataCollectionEnum.XFORM2));
        String expected = "https://xform2/visualiser/MODEL/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testFallbackToLunaticNormalWhenTargetIsNull_defaultAccessUrl() {
        String url = component.getAccessUrl(UserRoles.REVIEWER, mockQuestioning(), mockPartitioning(null));
        String expected = "https://lunatic-normal/v3/review/questionnaire/MODEL/unite-enquetee/SURVEYID";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    void testLunaticNullRole_returnsEmptyString() {
        String url = component.getAccessUrl(null, mockQuestioning(), mockPartitioning(DataCollectionEnum.LUNATIC_NORMAL));
        assertThat(url).isEmpty();
    }

    @Test
    void testXformNullRole_returnsEmptyString() {
        String url = component.getAccessUrl(null, mockQuestioning(), mockPartitioning(DataCollectionEnum.XFORM1));
        assertThat(url).isEmpty();
    }
}
