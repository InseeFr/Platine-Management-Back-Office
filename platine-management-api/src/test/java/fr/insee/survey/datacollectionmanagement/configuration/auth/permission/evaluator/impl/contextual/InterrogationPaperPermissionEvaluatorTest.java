package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterrogationPaperPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    QuestioningService questioningService;

    @Mock
    Authentication authentication;

    InterrogationPaperPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationPaperPermissionEvaluator(
                globalPermissionChecker,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    // ------------------------------------------------------------------
    // metadata
    // ------------------------------------------------------------------

    @Test
    void shouldReturnInterrogationPaperPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.INTERROGATION_ACCESS_IN_PAPER_MODE);
    }

    @Test
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    // ------------------------------------------------------------------
    // hasPermission
    // ------------------------------------------------------------------

    @Test
    void shouldReturnFalseWhenGlobalRoleIsNotValid() {
        when(globalPermissionChecker.hasPermission(
                authentication,
                Permission.INTERROGATION_ACCESS_IN_PAPER_MODE
        )).thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.INTERROGATION_ACCESS_IN_PAPER_MODE);
        verifyNoInteractions(questioningService);
    }

    @Test
    void shouldReturnFalseWhenRoleIsValidButBusinessRuleFails() {
        when(globalPermissionChecker.hasPermission(
                authentication,
                Permission.INTERROGATION_ACCESS_IN_PAPER_MODE
        )).thenReturn(true);
        when(questioningService.canWriteInPaperMode(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.INTERROGATION_ACCESS_IN_PAPER_MODE);
        verify(questioningService)
                .canWriteInPaperMode(questioningId);
    }

    @Test
    void shouldReturnTrueWhenRoleIsValidAndBusinessRulePasses() {
        when(globalPermissionChecker.hasPermission(
                authentication,
                Permission.INTERROGATION_ACCESS_IN_PAPER_MODE
        )).thenReturn(true);
        when(questioningService.canWriteInPaperMode(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isTrue();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.INTERROGATION_ACCESS_IN_PAPER_MODE);
        verify(questioningService)
                .canWriteInPaperMode(questioningId);
    }
}
