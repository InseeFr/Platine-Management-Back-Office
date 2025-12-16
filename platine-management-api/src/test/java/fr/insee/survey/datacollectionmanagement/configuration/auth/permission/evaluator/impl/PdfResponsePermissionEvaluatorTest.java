package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
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
class PdfResponsePermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    QuestioningService questioningService;

    @Mock
    Authentication authentication;

    PdfResponsePermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new PdfResponsePermissionEvaluator(
                globalPermissionChecker,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    @Test
    void shouldReturnReadPdfResponseAsPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.READ_PDF_RESPONSE);
    }

    @Test
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    @Test
    void shouldReturnFalseWhenGlobalPermissionIsDenied() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.READ_PDF_RESPONSE))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isFalse();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.READ_PDF_RESPONSE);
        verifyNoInteractions(questioningService);
    }

    @Test
    void shouldReturnFalseWhenQuestioningIsNotInBusinessSource() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.READ_PDF_RESPONSE))
                .thenReturn(true);
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isFalse();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.READ_PDF_RESPONSE);
        verify(questioningService)
                .canExportQuestioningDataToPdf(questioningId);
    }

    @Test
    void shouldReturnTrueWhenGlobalPermissionGrantedAndQuestioningIsInBusinessSource() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.READ_PDF_RESPONSE))
                .thenReturn(true);
        when(questioningService.canExportQuestioningDataToPdf(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result)
                .isTrue();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.READ_PDF_RESPONSE);
        verify(questioningService)
                .canExportQuestioningDataToPdf(questioningId);
    }
}
