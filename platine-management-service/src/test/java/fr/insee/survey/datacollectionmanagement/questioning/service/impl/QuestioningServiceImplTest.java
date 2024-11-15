package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class QuestioningServiceImplTest {

    @InjectMocks
    QuestioningServiceImpl questioningService;


    @Test
    @DisplayName("Check the V1 url in interviewer mode")
    void getV1UrlInterviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.INTERVIEWER;
        String modelName = "m1";
        String surveyUnitId = "999999999";
        String url= questioningService.buildV1Url(baseUrl, role, modelName, surveyUnitId);
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
        String url= questioningService.buildV1Url(baseUrl, role, modelName, surveyUnitId);
        String expected = "https://urlBase/visualiser/m1/999999999";
        assertThat(url).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check the V2 url in interviewer mode")
    void getV2UrlInterviewer() {
        String baseUrl = "https://urlBase";
        String role = UserRoles.INTERVIEWER;
        String modelName = "model";
        String surveyUnitId = "999999999";
        String url= questioningService.buildV2Url(baseUrl, role, modelName, surveyUnitId);
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
        String url= questioningService.buildV2Url(baseUrl, role, modelName, surveyUnitId);
        String expected = "https://urlBase/readonly/questionnaire/model/unite-enquetee/999999999";
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
        String url= questioningService.buildV3Url(baseUrl, role, modelName, surveyUnitId, sourceId, questioningId);
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
        String url= questioningService.buildV3Url(baseUrl, role, modelName, surveyUnitId, sourceId, questioningId);
        String expected = "https://urlBase/v3/review/questionnaire/model/unite-enquetee/999999999";
        assertThat(url).isEqualTo(expected);
    }

}