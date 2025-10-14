package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.configuration.FixedTimeConfiguration;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventSource;
import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitEventType;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(FixedTimeConfiguration.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SurveyUnitEventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SurveyUnitEventService surveyUnitEventService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when payload is invalid")
    void shouldReturn400WhenPayloadIsInvalid() throws Exception {
        // Invalid JSON: missing required fields / blank strings
        String invalidJson = """
                {
                  "campaignId": "   ",
                  "eventType": null,
                  "source": null,
                  "eventDate": null
                }
                """;

        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create and return events")
    void shouldCreateAndReturnEvents() throws Exception {
        // given
        String jsonBody = """
            {
              "campaignId": "%s",
              "eventType": "%s",
              "source": "%s",
              "eventDate": %s
            }
            """;

        // when / then
        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(jsonBody, "SOURCE22023T04", SurveyUnitEventType.CESSATION_IN_PROGRESS, SurveyUnitEventSource.ENTERPRISE, "1760368478660"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(jsonBody, "SOURCE22023T04", SurveyUnitEventType.RESTRUCTURING, SurveyUnitEventSource.OTHER_SOURCE, "1760368478662"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(jsonBody, "SOURCE22023T04", SurveyUnitEventType.TEMPORARY_INACTIVITY, SurveyUnitEventSource.SIRUS, "1760368478661"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(jsonBody, "SOURCE22023T03", SurveyUnitEventType.PERMANENT_CESSATION, SurveyUnitEventSource.OTHER_SOURCE, "1760368478663"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String jsonResult = mockMvc.perform(get(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResult = """
                [
                    {
                      "campaignId": "SOURCE22023T04",
                      "eventType": "CESSATION_IN_PROGRESS",
                      "source": "ENTERPRISE",
                      "eventDate": 1760368478660,
                      "eventCreationDate": 1747395350727
                    },
                    {
                      "campaignId": "SOURCE22023T04",
                      "eventType": "TEMPORARY_INACTIVITY",
                      "source": "SIRUS",
                      "eventDate": 1760368478661,
                      "eventCreationDate": 1747395350727
                    },
                    {
                      "campaignId": "SOURCE22023T03",
                      "eventType": "PERMANENT_CESSATION",
                      "source": "OTHER_SOURCE",
                      "eventDate": 1760368478663,
                      "eventCreationDate":1747395350727
                    }
                ]""";
        JSONAssert.assertEquals(expectedResult, jsonResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @DisplayName("Should return 404 Not Found when campaign is not linked to survey unit")
    void shouldReturn404WhenCampaignNotLinkedToSurveyUnit() throws Exception {
        // given
        String jsonBody = """
        {
          "campaignId": "UNKNOWN_CAMPAIGN",
          "eventType": "TEMPORARY_INACTIVITY",
          "source": "SIRUS",
          "eventDate": 1760368478661
        }
        """;

        // when / then
        mockMvc.perform(post(UrlConstants.API_SURVEY_UNITS_ID_EVENTS, "100000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
