package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
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
class InterrogationPaperDataEditPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    QuestioningService questioningService;

    InterrogationPaperDataEditPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationPaperDataEditPermissionEvaluator(
                globalPermissionChecker,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should expose INTERROGATION_PAPER_DATA_EDIT as handled permission")
    void shouldReturnInterrogationPaperPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.INTERROGATION_PAPER_DATA_EDIT);
    }

    @Test
    @DisplayName("should declare UUID as target type")
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    @Test
    @DisplayName("should deny when user does not have required roles (and should not call business service)")
    void shouldReturnFalseWhenGlobalRoleIsNotValid() {
        ProfiledAuthenticationToken profiledAuthenticationToken = AuthenticationUserProvider.getAuthenticatedUser(
                "user",
                AuthorityRoleEnum.PORTAL);
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isFalse();
        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT);
        verifyNoInteractions(questioningService);
    }

    @Test
    @DisplayName("should deny when roles are valid but questioning is not in paper environment")
    void shouldReturnFalseWhenRoleIsValidButBusinessRuleFails() {
        ProfiledAuthenticationToken profiledAuthenticationToken = AuthenticationUserProvider.getAuthenticatedUser(
                "user",
                AuthorityRoleEnum.INTERNAL_USER);
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT))
                .thenReturn(true);
        when(questioningService.canWriteInPaperEnvironment(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT);
        verify(questioningService).canWriteInPaperEnvironment(questioningId);
    }

    @Test
    @DisplayName("should grant when roles are valid and questioning is in paper environment")
    void shouldReturnTrueWhenRoleIsValidAndBusinessRulePasses() {
        ProfiledAuthenticationToken profiledAuthenticationToken = AuthenticationUserProvider.getAuthenticatedUser(
                "user",
                AuthorityRoleEnum.INTERNAL_USER);
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT))
                .thenReturn(true);
        when(questioningService.canWriteInPaperEnvironment(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isTrue();

        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_PAPER_DATA_EDIT);
        verify(questioningService).canWriteInPaperEnvironment(questioningId);
    }
}
