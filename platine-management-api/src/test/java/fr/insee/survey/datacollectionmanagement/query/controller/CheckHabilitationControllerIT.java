package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CheckHabilitationControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CheckHabilitationService checkHabilitationService;

    private final UUID questioningId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-000000000001");


    @Test
    void shouldAllowAccessForAdmin() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("ADMIN", AuthorityRoleEnum.ADMIN));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldAllowAccessForRespondent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("CONT1", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldNotAllowAccessForRespondent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("NOTHAB", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION)
                        .param("id", String.valueOf(questioningId))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

    @Test
    void shouldAllowAccessForAdminV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("ADMIN", AuthorityRoleEnum.ADMIN));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "id")
                        .param("campaign", "campaign")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldAllowAccessForRespondentV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("CONT1", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "100000000")
                        .param("campaign", "SOURCE12023T01")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldNotAllowAccessForRespondentV1() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("NOTHAB", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_HABILITATION_V1)
                        .param("id", "100000000")
                        .param("campaign", "SOURCE12023T01")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

    @Test
    void shouldNotAllowPermissionAccessForRespondent() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("NOTHAB", AuthorityRoleEnum.RESPONDENT));
        mockMvc.perform(get(UrlConstants.API_CHECK_PERMISSION)
                        .param("id", UUID.randomUUID().toString())
                        .param("permission", Permission.INTERROGATION_EXPORT_PDF_DATA.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

    @ParameterizedTest(name = "Permission {0} should be allowed for role {1}")
    @MethodSource("globalPermissionsAndRoles")
    void shouldAllowGlobalPermissionAccessForAuthorizedRoles(
            Permission permission,
            AuthorityRoleEnum role
    ) throws Exception {

        // given
        SecurityContextHolder.getContext().setAuthentication(
                AuthenticationUserProvider.getAuthenticatedUser(
                        "USER",
                        role
                )
        );

        // when / then
        mockMvc.perform(get(UrlConstants.API_CHECK_PERMISSION)
                        .param("permission", permission.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }


    @Test
    void shouldAllowPermissionAccessForInternalUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("GESTIO1", AuthorityRoleEnum.INTERNAL_USER));
        mockMvc.perform(get(UrlConstants.API_CHECK_PERMISSION)
                        .param("id", "bbbbbbbb-bbbb-bbbb-bbbb-000000000002")
                        .param("permission", Permission.INTERROGATION_EXPORT_PDF_DATA.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(true));
    }

    @Test
    void shouldNotAllowPermissionAccessForInternalUserWhenNoBusinessSource() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUser("GESTIO1", AuthorityRoleEnum.INTERNAL_USER));
        mockMvc.perform(get(UrlConstants.API_CHECK_PERMISSION)
                        .param("id", "bbbbbbbb-bbbb-bbbb-bbbb-000000000001")
                        .param("permission", Permission.INTERROGATION_EXPORT_PDF_DATA.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(false));
    }

    @ParameterizedTest(
            name = "[{index}] {2} (id={0} → habilitated={1})"
    )
    @MethodSource("paperPermissionCases")
    void shouldCheckPaperPermission(String id,
                                    boolean expectedHabilitated,
                                    String description) throws Exception {

        // Given
        SecurityContextHolder.getContext().setAuthentication(
                AuthenticationUserProvider.getAuthenticatedUser(
                        "GESTIO1",
                        AuthorityRoleEnum.INTERNAL_USER
                )
        );

        // When / Then
        mockMvc.perform(get(UrlConstants.API_CHECK_PERMISSION)
                        .param("id", id)
                        .param("permission", Permission.INTERROGATION_ACCESS_IN_PAPER_MODE.name())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitated").value(expectedHabilitated));
    }

    private static Stream<Arguments> paperPermissionCases() {
        return Stream.of(
                Arguments.of(
                        "bbbbbbbb-bbbb-bbbb-bbbb-000000000003",
                        false,
                        "Paper source allowed but user is not authorized"
                ),
                Arguments.of(
                        "bbbbbbbb-bbbb-bbbb-bbbb-000000000000",
                        false,
                        "Questioning event is forbidden"
                ),
                Arguments.of(
                        "bbbbbbbb-bbbb-bbbb-bbbb-000000000002",
                        false,
                        "Source is not paper-based"
                )
        );
    }


    static Stream<Arguments> globalPermissionsAndRoles() {
        return Arrays.stream(Permission.values())
                .filter(Permission::global)
                .flatMap(permission ->
                        permission.allowedRoles().stream()
                                .map(role -> Arguments.of(permission, role))
                );
    }


}