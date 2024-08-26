package fr.insee.survey.datacollectionmanagement.questioning.util;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum.POOL1;
import static fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum.POOL2;
import static org.assertj.core.api.Assertions.assertThat;

class QuestioningUrlResolverTest {

        QuestioningUrlResolver questioningUrlResolver;
        ApplicationConfig applicationConfig;

        @BeforeEach
        void init() {

            applicationConfig = new ApplicationConfig();
            applicationConfig.setQuestioningUrlStromaeV1Pool1("https://urlV1Pool1");
            applicationConfig.setQuestioningUrlStromaeV1Pool2("https://urlV1Pool2");
            applicationConfig.setQuestioningUrlStromaeV2Pool1("https://urlV2Pool1");
            applicationConfig.setQuestioningUrlStromaeV3Pool1("https://urlV3Pool1");

            questioningUrlResolver = new QuestioningUrlResolver(applicationConfig);
        }

        @Test
        @DisplayName("Resolve URL for V1 and pool1")
        void resolveUrlForV1Pool1() {
            String typeUrl = UrlTypeEnum.V1.name();
            String pool = POOL1.name();

            String url = questioningUrlResolver.resolveUrl(typeUrl, pool);

            assertThat(url).isEqualTo(applicationConfig.getQuestioningUrlStromaeV1Pool1());
        }
        @Test
        @DisplayName("Resolve URL for V1 and pool2")
        void resolveUrlForV1Pool2() {
            String typeUrl = UrlTypeEnum.V1.name();
            String pool = POOL2.name();

            String url = questioningUrlResolver.resolveUrl(typeUrl, pool);

            assertThat(url).isEqualTo(applicationConfig.getQuestioningUrlStromaeV1Pool2());
        }

        @Test
        @DisplayName("Resolve URL for V2 and pool1")
        void resolveUrlForV2Pool1() {
            String typeUrl = UrlTypeEnum.V2.name();
            String pool = POOL1.name();

            String url = questioningUrlResolver.resolveUrl(typeUrl, pool);

            assertThat(url).isEqualTo(applicationConfig.getQuestioningUrlStromaeV2Pool1());
        }

        @Test
        @DisplayName("Resolve URL for V3 and pool1")
        void resolveUrlForV3Pool1() {
            String typeUrl = UrlTypeEnum.V3.name();
            String pool = POOL1.name();

            String url = questioningUrlResolver.resolveUrl(typeUrl, pool);

            assertThat(url).isEqualTo(applicationConfig.getQuestioningUrlStromaeV3Pool1());
        }

        @Test
        @DisplayName("Resolve URL returns empty string for unknown combination")
        void resolveUrlForUnknownCombination() {
            String typeUrl = "V4";  // Unsupported typeUrl
            String pool = "POOL3";  // Unsupported pool

            String url = questioningUrlResolver.resolveUrl(typeUrl, pool);
            String expected = "";

            assertThat(url).isEqualTo(expected);
        }
        @Test
        @DisplayName("Check the V1 url in interviewer mode")
        void getV1UrlInterviewer() {
            String baseUrl = "https://urlBase";
            String role = UserRoles.INTERVIEWER;
            String campaignName = "source-year-period";
            String surveyUnitId = "999999999";

            UrlParameters params = new UrlParameters(baseUrl, role, null, surveyUnitId, null,campaignName);

            String url = questioningUrlResolver.buildV1Url(params);
            String expected = "https://urlBase/repondre/source-year-period/999999999";
            assertThat(url).isEqualTo(expected);
        }

        @Test
        @DisplayName("Check the V1 url in reviewer mode")
        void getV1UrlReviewer() {
            String baseUrl = "https://urlBase";
            String role = UserRoles.REVIEWER;
            String surveyUnitId = "999999999";
            String campaignName = "source-year-period";

            UrlParameters params = new UrlParameters(baseUrl, role, null, surveyUnitId, null,campaignName);

            String url = questioningUrlResolver.buildV1Url(params);
            String expected = "https://urlBase/visualiser/source-year-period/999999999";
            assertThat(url).isEqualTo(expected);
        }

        @Test
        @DisplayName("Check the V2 url in interviewer mode")
        void getV2UrlInterviewer() {
            String baseUrl = "https://urlBase";
            String role = UserRoles.INTERVIEWER;
            String modelName = "model";
            String surveyUnitId = "999999999";
            Questioning questioning = new Questioning();
            questioning.setModelName(modelName);
            UrlParameters params = new UrlParameters(baseUrl, role, questioning, surveyUnitId, null, null);

            String url = questioningUrlResolver.buildV2Url(params);
            String expected = "https://urlBase/questionnaire/model/unite-enquetee/999999999";
            assertThat(url).isEqualTo(expected);
        }

        @Test
        @DisplayName("Check the V2 url in reviewer mode")
        void getV2UrlReviewer() {
            String baseUrl = "https://urlBase";
            String role = UserRoles.REVIEWER;
            String modelName = "model";
            String surveyUnitId = "999999999";
            Questioning questioning = new Questioning();
            questioning.setModelName(modelName);
            UrlParameters params = new UrlParameters(baseUrl, role, questioning, surveyUnitId, null, null);

            String url = questioningUrlResolver.buildV2Url(params);
            String expected = "https://urlBase/readonly/questionnaire/model/unite-enquetee/999999999";
            assertThat(url).isEqualTo(expected);
        }

        @Test
        @DisplayName("Check the V3 url in interviewer mode")
        void getV3UrlInterviewer() {
            String baseUrl = "https://urlBase/v3";
            String role = UserRoles.INTERVIEWER;
            String modelName = "model";
            String surveyUnitId = "999999999";
            String sourceId = "enq";
            Long questioningId = 123456789L;
            Questioning questioning = new Questioning();
            questioning.setModelName(modelName);
            questioning.setId(questioningId);
            UrlParameters params = new UrlParameters(baseUrl, role, questioning, surveyUnitId, sourceId, null);

            String url = questioningUrlResolver.buildV3Url(params);
            String expected = "https://urlBase/v3/questionnaire/model/unite-enquetee/999999999?pathLogout=%2Fenq&pathAssistance=%2Fenq%2Fcontacter-assistance%2Fauth%3FquestioningId%3D123456789";
            assertThat(url).isEqualTo(expected);
        }

        @Test
        @DisplayName("Check the V3 url in reviewer mode")
        void getV3UrlReviewer() {
            String baseUrl = "https://urlBase/v3";
            String role = UserRoles.REVIEWER;
            String modelName = "model";
            String surveyUnitId = "999999999";
            String sourceId = "enq";
            Long questioningId = 123456789L;
            Questioning questioning = new Questioning();
            questioning.setModelName(modelName);
            questioning.setId(questioningId);
            UrlParameters params = new UrlParameters(baseUrl, role, questioning, surveyUnitId, sourceId, null);

            String url = questioningUrlResolver.buildV3Url(params);
            String expected = "https://urlBase/v3/review/questionnaire/model/unite-enquetee/999999999";
            assertThat(url).isEqualTo(expected);
        }
    }