package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WebclientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    @DisplayName("Should get all questionings")
    void getQuestionings() throws Exception {
        // GIVEN
        final String modelName = "m0";
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(
                get(Constants.API_WEBCLIENT_QUESTIONINGS)
                        .param("modelName", modelName)
                        .param("idPartitioning", idPartitioning)
                        .param("idSurveyUnit", idSurveyUnit)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPartitioning").value(idPartitioning))
                .andExpect(jsonPath("$.modelName").value(modelName))
                .andExpect(jsonPath("$.surveyUnit.idSu").value(idSurveyUnit));
    }

    @Test
    @DisplayName("Should get metadata")
    void getMetadata() throws Exception {
        // GIVEN
        final String id = "SOURCE12023T2000";

        // WHEN
        this.mockMvc.perform(get(Constants.API_WEBCLIENT_METADATA_ID, id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source.id").value("SOURCE1"));
    }

    @Test
    @DisplayName("Should get main contact")
    void getMainContact() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(
                get(Constants.API_MAIN_CONTACT)
                        .param("partitioning", idPartitioning)
                        .param("survey-unit", idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("CONT1"));

        this.mockMvc.perform(
                        get(Constants.API_MAIN_CONTACT)
                                .param("partitioning", idPartitioning)
                                .param("survey-unit", "wrong_id"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should put new questioning with new contact")
    void putQuestioning() throws Exception {
        // GIVEN
        String requestBody = """
            {
                "idPartitioning": "SOURCE12023T1000",
                "modelName": "m0",
                "surveyUnit": {
                    "idSu": "100000000",
                    "address": {
                        "streetNumber": 1,
                        "streetName": "UpdatedStreet",
                        "zipCode": "UpdatedPostalCode",
                        "cityName": "UpdatedCity"
                    }
                },
                "contacts":  [
                    {
                        "identifier": "CONT1",
                        "address": {
                            "streetNumber": 1,
                            "streetName": "UpdatedStreet",
                            "zipCode": "UpdatedPostalCode",
                            "cityName": "UpdatedCity"
                        },
                        "email": "updated@example.com",
                        "phone": "0602020202",
                        "externalId": "UpdatedExternalId"
                    }
                ]
            }
        """;

        // WHEN
        this.mockMvc.perform(
                put(Constants.API_WEBCLIENT_QUESTIONINGS)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surveyUnit.address.streetName").value("UpdatedStreet"))
                .andExpect(jsonPath("$.contacts", hasSize(4)))
                .andExpect(jsonPath("$.contacts[?(@.identifier == 'CONT1')].externalId").value("UpdatedExternalId"));
    }

    @Test
    @DisplayName("Should state of the last questioningEvent")
    void getState() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(get(Constants.API_WEBCLIENT_STATE, idPartitioning, idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(TypeQuestioningEvent.VALINT.toString()));
    }

    @Test
    @DisplayName("Should put metadata of partitioning")
    void putMetadata() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12025T1000";
        final String requestBody = """
            {
               "idPartitioning":"SOURCE12025T1000",
               "partitioning":{
                  "id":"SOURCE12025T1000",
                  "campaignId":"campaignId",
                  "label":"label",
                  "openingDate":"2023-01-01T00:00:00.000+00:00",
                  "closingDate":"2023-12-31T00:00:00.000+00:00"
               },
               "campaign":{
                  "id":"campaignId",
                  "surveyId":"surveyId",
                  "year":2025,
                  "campaignWording":"campaignWording",
                  "period":"A00"
               },
               "survey":{
                  "id":"surveyId",
                  "sourceId":"sourceId",
                  "year":2025,
                  "sampleSize":100,
                  "longWording":"longWording",
                  "shortWording":"shortWording",
                  "shortObjectives":"shortObjectives",
                  "longObjectives":"longObjectives",
                  "visaNumber":"visaNumber",
                  "cnisUrl":"cnisUrl",
                  "diffusionUrl":"diffusionUrl",
                  "noticeUrl":"noticeUrl",
                  "specimenUrl":"specimenUrl",
                  "communication":"communication"
               },
               "source":{
                  "id":"sourceId",
                  "mandatoryMySurveys":false,
                  "longWording":"longWording",
                  "shortWording":"shortWording",
                  "periodicity":"A"
               },
               "owner":{
                  "id":"ownerId"
               },
               "support":{
                  "id":"supportId"
               }
            }
        """;

        // WHEN
        this.mockMvc.perform(
                        put(Constants.API_WEBCLIENT_METADATA_ID, idPartitioning)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partitioning.id").value("SOURCE12025T1000"))
                .andExpect(jsonPath("$.campaign.id").value("campaignId"))
                .andExpect(jsonPath("$.survey.id").value("surveyId"))
                .andExpect(jsonPath("$.source.id").value("sourceId"))
                .andExpect(jsonPath("$.owner.id").value("ownerId"))
                .andExpect(jsonPath("$.support.id").value("supportId"));
    }

    @Test
    @DisplayName("Should add FOLLOWUP state to a questioning")
    void postFollowUp() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(post(Constants.API_WEBCLIENT_FOLLOWUP, idPartitioning, idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(TypeQuestioningEvent.FOLLOWUP.toString()));
    }

    @Test
    @DisplayName("Should indicate whether a questioning should be follow up or not")
    void isToFollowUp() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(get(Constants.API_WEBCLIENT_FOLLOWUP, idPartitioning, idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value("false"));
    }

    @Test
    @DisplayName("Should indicate whether a questioning should be extract or not")
    void isToExtract() throws Exception {
        // GIVEN
        final String idPartitioning = "SOURCE12023T1000";
        final String idSurveyUnit = "100000000";

        // WHEN
        this.mockMvc.perform(get(Constants.API_WEBCLIENT_EXTRACT, idPartitioning, idSurveyUnit))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value("true"));
    }
}
