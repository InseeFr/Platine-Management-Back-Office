package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterrogationDataPermissionRulesTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    QuestioningAccreditationService questioningAccreditationService;

    @Mock
    QuestioningService questioningService;

    @Mock
    ProfiledAuthenticationToken authentication;

    InterrogationDataPermissionRules rules;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        rules = new InterrogationDataPermissionRules(
                globalPermissionChecker,
                questioningAccreditationService,
                questioningService
        );
        questioningId = UUID.randomUUID();
        when(authentication.getName()).thenReturn("user");
    }

    @Test
    @DisplayName("should deny when user does not have acceptable roles for the permission")
    void shouldDenyWhenGlobalRoleIsNotValid() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(false);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isFalse();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verifyNoInteractions(questioningAccreditationService, questioningService);
        verify(authentication, never()).hasRole(any());
    }

    @Test
    @DisplayName("should grant when user is admin")
    void shouldGrantWhenAdmin() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(true);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isTrue();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verifyNoInteractions(questioningAccreditationService, questioningService);
        verify(authentication, never()).hasRole(AuthorityRoleEnum.RESPONDENT);
    }

    @Test
    @DisplayName("should deny when questioning is validated in paper environment (non-admin)")
    void shouldDenyWhenPaperValidated() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(questioningService.isValidatedInPaperEnvironment(questioningId))
                .thenReturn(true);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isFalse();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(questioningService).isValidatedInPaperEnvironment(questioningId);
        verifyNoInteractions(questioningAccreditationService);
        verify(authentication, never()).hasRole(AuthorityRoleEnum.RESPONDENT);
    }

    @Test
    @DisplayName("should grant for respondent when accredited and not validated in paper environment")
    void shouldGrantRespondentWhenAccredited() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(questioningService.isValidatedInPaperEnvironment(questioningId))
                .thenReturn(false);
        when(authentication.hasRole(AuthorityRoleEnum.RESPONDENT))
                .thenReturn(true);
        when(questioningAccreditationService.hasAccreditation(questioningId, "USER"))
                .thenReturn(true);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isTrue();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(questioningService).isValidatedInPaperEnvironment(questioningId);
        verify(authentication).hasRole(AuthorityRoleEnum.RESPONDENT);
        verify(questioningAccreditationService).hasAccreditation(questioningId, "USER");
    }

    @Test
    @DisplayName("should deny for respondent when not accredited and not validated in paper environment")
    void shouldDenyRespondentWhenNotAccredited() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(questioningService.isValidatedInPaperEnvironment(questioningId))
                .thenReturn(false);
        when(authentication.hasRole(AuthorityRoleEnum.RESPONDENT))
                .thenReturn(true);
        when(questioningAccreditationService.hasAccreditation(questioningId, "USER"))
                .thenReturn(false);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isFalse();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(questioningService).isValidatedInPaperEnvironment(questioningId);
        verify(authentication).hasRole(AuthorityRoleEnum.RESPONDENT);
        verify(questioningAccreditationService).hasAccreditation(questioningId, "USER");
    }

    @Test
    @DisplayName("should grant for non-respondent user when roles are valid and not paper-validated (e.g., internal user for READ)")
    void shouldGrantNonRespondentWhenRolesAreValid() {
        // Given
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(questioningService.isValidatedInPaperEnvironment(questioningId))
                .thenReturn(false);
        when(authentication.hasRole(AuthorityRoleEnum.RESPONDENT))
                .thenReturn(false);

        // When
        boolean result = rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);

        // Then
        assertThat(result).isTrue();
        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_DATA_READ);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(questioningService).isValidatedInPaperEnvironment(questioningId);
        verify(authentication).hasRole(AuthorityRoleEnum.RESPONDENT);
        verifyNoInteractions(questioningAccreditationService);
    }
}
