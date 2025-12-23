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
class InterrogationExportPdfDataPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker roleChecker;

    @Mock
    QuestioningService questioningService;

    @Mock
    Authentication authentication;

    InterrogationExportPdfDataPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationExportPdfDataPermissionEvaluator(
                roleChecker,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    @Test
    void shouldReturnReadPdfResponseAsPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.INTERROGATION_EXPORT_PDF_DATA);
    }

    @Test
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    @Test
    void shouldReturnFalseWhenGlobalPermissionIsDenied() {
        when(roleChecker.hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isFalse();

        verify(roleChecker)
                .hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA);
        verifyNoInteractions(questioningService);
    }

    @Test
    void shouldReturnFalseWhenQuestioningIsNotInBusinessSource() {
        when(roleChecker.hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA))
                .thenReturn(true);
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isFalse();

        verify(roleChecker)
                .hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA);
        verify(questioningService)
                .canExportQuestioningDataToPdf(questioningId);
    }

    @Test
    void shouldReturnTrueWhenGlobalPermissionGrantedAndQuestioningIsInBusinessSource() {
        when(roleChecker.hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA))
                .thenReturn(true);
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isTrue();

        verify(roleChecker)
                .hasPermission(authentication, Permission.INTERROGATION_EXPORT_PDF_DATA);
        verify(questioningService)
                .canExportQuestioningDataToPdf(questioningId);
    }
}
