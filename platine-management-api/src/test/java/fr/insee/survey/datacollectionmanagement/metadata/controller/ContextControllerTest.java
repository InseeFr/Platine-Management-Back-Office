package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.ContextCreateDto;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.ContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContextControllerTest {

    private MockMvc mockMvc;
    private ContextService contextService;

    @BeforeEach
    void setup() {
        contextService = mock(ContextService.class);
        ContextController contextController = new ContextController(contextService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(contextController)
                .build();
    }

    @Test
    void postContext_should_deserialize_and_map_everything() throws Exception {
        // Given
        UUID ctxId = UUID.randomUUID();

        String json = """
        {
          "id": "%s",
          "shortLabel": "CAMP123",
          "label": "Campaign wording",
          "context": "HOUSEHOLD",
          "metadatas": {
            "statisticalOperationSerieShortLabel": "SRC_SERIE",
            "statisticalOperationSerieLabel": "Source long wording",
            "statisticalOperationShortLabel": "SURV_OP",
            "statisticalOperationLabel": "Survey long wording",
            "year": 2025,
            "shortObjectives": "Short obj",
            "visaNumber": "V-42",
            "periodicity": "A",
            "period": "A00"
          },
          "partitions": [
            {
              "partitionShortLabel": "P1",
              "partitionId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
              "partitionLabel": "Partition 1",
              "collectionStartDate": "2025-01-10T00:00:00Z",
              "collectionEndDate":   "2025-02-20T00:00:00Z",
              "returnDate":          "2025-03-01T00:00:00Z",
              "partitionModes": ["CAWI"],
              "unitType": "HOUSING",
              "questionnaireModels": ["questionnaire1"],
              "communicationSteps": [
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1",
                  "communicationLabel": "Label1",
                  "communicationModel": "Model1",
                  "communicationType": "NOTICE",
                  "communicationMedium": "LETTER",
                  "communicationDate": "2025-01-05T00:00:00Z",
                  "communicationModelMetadata": [
                      { "key": "longObjectives",        "type": "string",  "value": "LOREM LONG" },
                      { "key": "visaNumber",            "type": "string",  "value": "V-999" },
                      { "key": "cnisUrl",               "type": "string",  "value": "https://cnis.example" },
                      { "key": "diffusionUrl",          "type": "string",  "value": "https://diff.example" },
                      { "key": "rgpdInformation",       "type": "string",  "value": "RGPD bla bla" },
                      { "key": "questionnaireNumber",   "type": "string",  "value": "Q-42" },
                      { "key": "surveyStatus",          "type": "string",  "value": "RUNNING" },
                      { "key": "surveyCode",            "type": "string",  "value": "SVI-1234" },
                      { "key": "personalDataParagraph", "type": "boolean",  "value": true },
                      { "key": "compulsoryNature",      "type": "boolean", "value": true },
                      { "key": "svi",                   "type": "boolean", "value": true }
                  ]
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2",
                  "communicationLabel": "Label2",
                  "communicationModel": "Model2",
                  "communicationType": "NOTICE",
                  "communicationMedium": "EMAIL",
                  "communicationDate": "2025-01-06T00:00:00Z",
                  "communicationModelMetadata": []
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb3",
                  "communicationLabel": "Label3",
                  "communicationModel": "Model3",
                  "communicationType": "REMINDER",
                  "communicationMedium": "LETTER",
                  "communicationDate": "2025-01-15T00:00:00Z",
                  "communicationModelMetadata": []
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb4",
                  "communicationLabel": "Label4",
                  "communicationModel": "Model4",
                  "communicationType": "REMINDER",
                  "communicationMedium": "LETTER",
                  "communicationDate": "2025-01-25T00:00:00Z",
                  "communicationModelMetadata": []
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb5",
                  "communicationLabel": "Label5",
                  "communicationModel": "Model5",
                  "communicationType": "REMINDER",
                  "communicationMedium": "EMAIL",
                  "communicationDate": "2025-01-18T00:00:00Z",
                  "communicationModelMetadata": []
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb6",
                  "communicationLabel": "Label6",
                  "communicationModel": "Model6",
                  "communicationType": "FORMAL_NOTICE",
                  "communicationMedium": "LETTER",
                  "communicationDate": "2025-02-10T00:00:00Z",
                  "communicationModelMetadata": []
                },
                {
                  "communicationId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb7",
                  "communicationLabel": "Label7",
                  "communicationModel": "Model7",
                  "communicationType": "NON_RESPONSE",
                  "communicationMedium": "LETTER",
                  "communicationDate": "2025-03-10T00:00:00Z",
                  "communicationModelMetadata": []
                }
              ]
            },
            {
              "partitionShortLabel": "P2",
              "partitionId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
              "partitionLabel": "Partition 2",
              "collectionStartDate": "2025-04-01T00:00:00Z",
              "collectionEndDate":   "2025-04-30T00:00:00Z",
              "returnDate":          null,
              "partitionModes": ["CAWI", "PAPI"],
              "unitType": "HOUSING",
              "questionnaireModels": ["questionnaire1"],
              "communicationSteps": []
            },
            {
              "partitionShortLabel": "P3",
              "partitionId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbc",
              "partitionLabel": "Partition 3",
              "collectionStartDate": "2025-04-01T00:00:00Z",
              "collectionEndDate":   "2025-04-30T00:00:00Z",
              "returnDate":          null,
              "partitionModes": ["PAPI"],
              "unitType": "HOUSING",
              "questionnaireModels": ["questionnaire1"],
              "communicationSteps": []
            }
          ]
        }
        """.formatted(ctxId);

        // When
        mockMvc.perform(post("/api/context")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(authentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN))))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<ContextCreateDto> cap = ArgumentCaptor.forClass(ContextCreateDto.class);
        verify(contextService).saveContext(cap.capture());
        ContextCreateDto sent = cap.getValue();

        assertThat(sent.source().id()).isEqualTo("SRC_SERIE");
        assertThat(sent.source().type()).isEqualTo("HOUSEHOLD"); // vient de context.getContext()
        assertThat(sent.source().shortWording()).isEqualTo("SRC_SERIE");
        assertThat(sent.source().longWording()).isEqualTo("Source long wording");
        assertThat(sent.source().periodicity()).isEqualTo(PeriodicityEnum.A);
        assertThat(sent.source().personalData()).isTrue();

        assertThat(sent.survey().id()).isEqualTo("SURV_OP");
        assertThat(sent.survey().year()).isEqualTo(2025);
        assertThat(sent.survey().longWording()).isEqualTo("Survey long wording");
        assertThat(sent.survey().shortWording()).isEqualTo("SURV_OP");
        assertThat(sent.survey().longObjectives()).isEqualTo("LOREM LONG");
        assertThat(sent.survey().visaNumber()).isEqualTo("V-999");
        assertThat(sent.survey().cnisUrl()).isEqualTo("https://cnis.example");
        assertThat(sent.survey().diffusionUrl()).isEqualTo("https://diff.example");
        assertThat(sent.survey().compulsoryNature()).isTrue();
        assertThat(sent.survey().rgpdBlock()).isEqualTo("RGPD bla bla");
        assertThat(sent.survey().sendPaperQuestionnaire()).isEqualTo("Q-42");
        assertThat(sent.survey().surveyStatus()).isEqualTo("RUNNING");
        assertThat(sent.survey().sviUse()).isTrue();
        assertThat(sent.survey().sviNumber()).isEqualTo("SVI-1234");

        assertThat(sent.campaign().id()).isEqualTo("CAMP123");
        assertThat(sent.campaign().technicalId()).isEqualTo(ctxId);
        assertThat(sent.campaign().year()).isEqualTo(2025);
        assertThat(sent.campaign().campaignWording()).isEqualTo("Campaign wording");
        assertThat(sent.campaign().period()).isEqualTo("A00");
        assertThat(sent.campaign().periodCollect()).isEqualTo("A00");

        // Partitionings with CAWI modes
        assertThat(sent.partitionings()).hasSize(2);

        var p1 = sent.partitionings().get(0);
        assertThat(p1.id()).isEqualTo("P1");
        assertThat(p1.label()).isEqualTo("Partition 1");
        assertThat(p1.openingDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-10T00:00:00Z")));
        assertThat(p1.closingDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-02-20T00:00:00Z")));
        assertThat(p1.returnDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-03-01T00:00:00Z")));

        assertThat(p1.openingLetterDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-05T00:00:00Z")));
        assertThat(p1.openingMailDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-06T00:00:00Z")));
        assertThat(p1.formalNoticeDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-02-10T00:00:00Z")));
        assertThat(p1.noReplyDate()).isEqualTo(java.util.Date.from(Instant.parse("2025-03-10T00:00:00Z")));

        assertThat(p1.followupLetter1Date()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-15T00:00:00Z")));
        assertThat(p1.followupLetter2Date()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-25T00:00:00Z")));
        assertThat(p1.followupLetter3Date()).isNull();
        assertThat(p1.followupLetter4Date()).isNull();

        assertThat(p1.followupMail1Date()).isEqualTo(java.util.Date.from(Instant.parse("2025-01-18T00:00:00Z")));
        assertThat(p1.followupMail2Date()).isNull();
        assertThat(p1.followupMail3Date()).isNull();
        assertThat(p1.followupMail4Date()).isNull();

        var p2 = sent.partitionings().get(1);
        assertThat(p2.id()).isEqualTo("P2");
        assertThat(p2.label()).isEqualTo("Partition 2");
        assertThat(p2.returnDate()).isNull();

        assertThat(p2.openingLetterDate()).isNull();
        assertThat(p2.openingMailDate()).isNull();
        assertThat(p2.formalNoticeDate()).isNull();
        assertThat(p2.noReplyDate()).isNull();
        assertThat(p2.followupLetter1Date()).isNull();
        assertThat(p2.followupMail1Date()).isNull();
    }
}
