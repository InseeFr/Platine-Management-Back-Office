package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
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
class InterrogationDataExportPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    QuestioningService questioningService;

    @Mock
    ProfiledAuthenticationToken profiledAuthenticationToken;

    InterrogationDataExportPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationDataExportPermissionEvaluator(
                globalPermissionChecker,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should expose INTERROGATION_DATA_EXPORT as handled permission")
    void shouldReturnInterrogationDataExportAsPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.INTERROGATION_DATA_EXPORT);
    }

    @Test
    @DisplayName("should declare UUID as target type")
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    @Test
    @DisplayName("should deny when user does not have required roles (and should not call business service)")
    void shouldReturnFalseWhenGlobalPermissionIsDenied() {
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT))
                .thenReturn(false);
        when(profiledAuthenticationToken.getName()).thenReturn("user");

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT);
        verify(profiledAuthenticationToken).getName();
        verifyNoInteractions(questioningService);
    }

    @Test
    @DisplayName("should deny when roles are valid but export rule fails")
    void shouldReturnFalseWhenRoleIsValidButBusinessRuleFails() {
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT))
                .thenReturn(true);
        when(profiledAuthenticationToken.getName()).thenReturn("user");
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT);
        verify(profiledAuthenticationToken).getName();
        verify(questioningService).canExportQuestioningDataToPdf(questioningId);
    }

    @Test
    @DisplayName("should grant when roles are valid and export rule passes")
    void shouldReturnTrueWhenRoleIsValidAndBusinessRulePasses() {
        when(globalPermissionChecker.hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT))
                .thenReturn(true);
        when(profiledAuthenticationToken.getName()).thenReturn("user");
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(profiledAuthenticationToken, questioningId);

        assertThat(result).isTrue();

        verify(globalPermissionChecker)
                .hasPermission(profiledAuthenticationToken, Permission.INTERROGATION_DATA_EXPORT);
        verify(profiledAuthenticationToken).getName();
        verify(questioningService).canExportQuestioningDataToPdf(questioningId);
    }
}
