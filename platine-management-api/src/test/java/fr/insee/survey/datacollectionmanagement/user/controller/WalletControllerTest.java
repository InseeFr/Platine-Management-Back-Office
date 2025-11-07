package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import fr.insee.survey.datacollectionmanagement.user.validator.WalletValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static fr.insee.survey.datacollectionmanagement.constants.UrlConstants.API_SOURCE_ID_WALLET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void postWithoutAuthentication_returns401() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getNotAuthenticatedUser());
        mockMvc.perform(multipart(API_SOURCE_ID_WALLET, "SRC-001")
                        .file("file", "dummy".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postWithUserRole_returns403() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("user", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(multipart(API_SOURCE_ID_WALLET, "SRC-001")
                        .file("file", "dummy".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    void postWithAdmin_returns200_csv() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN));

        MockMultipartFile csv = new MockMultipartFile("file", "wallets.csv", "text/csv", "surveyUnit,internal_user,group\n100000007,USER1,G1".getBytes());

        mockMvc.perform(multipart(API_SOURCE_ID_WALLET, "SOURCE1")
                        .file(csv)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void postWithAdmin_returns200_json() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN));

        String json = """
            [
              {
                "surveyUnit": "100000007",
                "internal_user": "USER1",
                "group": "G1"
              }
            ]
            """;

        MockMultipartFile jsonFile = new MockMultipartFile(
                "file",                 // nom du champ attendu par le controller
                "wallets.json",         // nom du fichier
                "application/json",     // content type du fichier
                json.getBytes()
        );

        mockMvc.perform(multipart(API_SOURCE_ID_WALLET, "SOURCE1")
                        .file(jsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void postWithAdmin_invalidPayload_throwsWalletBusinessRuleException_returns400_andErrors() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("admin", AuthorityRoleEnum.ADMIN));
        String invalidJson = """
                [
                  {
                    "surveyUnit": "",
                    "internal_user": "",
                    "group": ""
                  }
                ]
                """;

        MockMultipartFile jsonFile = new MockMultipartFile(
                "file",
                "wallets.json",
                "application/json",
                invalidJson.getBytes()
        );

        mockMvc.perform(multipart(API_SOURCE_ID_WALLET, "SOURCE1")
                        .file(jsonFile)
                        .contentType("multipart/form-data"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Data"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(2));
    }
}