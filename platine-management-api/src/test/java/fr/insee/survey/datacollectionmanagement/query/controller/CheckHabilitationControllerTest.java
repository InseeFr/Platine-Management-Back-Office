package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.PermissionEvaluatorHandler;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckHabilitationControllerTest {

    @Mock
    CheckHabilitationService checkHabilitationService;

    @Mock
    PermissionEvaluatorHandler permissionEvaluatorHandler;

    @Mock
    Authentication authentication;

    @Test
    @DisplayName("should use INTERROGATION_DATA_EDIT when role is null")
    void shouldUseEditPermissionWhenRoleIsNull() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT))
                .thenReturn(true);

        HabilitationDto result = controller.checkHabilitation(null, questioningId, authentication);

        assertThat(result.isHabilitated()).isTrue();
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("should use INTERROGATION_DATA_EDIT when role is blank")
    void shouldUseEditPermissionWhenRoleIsBlank() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT))
                .thenReturn(false);

        HabilitationDto result = controller.checkHabilitation("   ", questioningId, authentication);

        assertThat(result.isHabilitated()).isFalse();
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("should map REVIEWER role to INTERROGATION_DATA_READ")
    void shouldMapReviewerToReadPermission() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);

        HabilitationDto result = controller.checkHabilitation(UserRoles.REVIEWER, questioningId, authentication);

        assertThat(result.isHabilitated()).isTrue();
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("should map INTERVIEWER role to INTERROGATION_DATA_EDIT")
    void shouldMapInterviewerToEditPermission() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT))
                .thenReturn(true);

        HabilitationDto result = controller.checkHabilitation(UserRoles.INTERVIEWER, questioningId, authentication);

        assertThat(result.isHabilitated()).isTrue();
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("should map EXPERT role to INTERROGATION_EXPERT_DATA_EDIT")
    void shouldMapExpertToExpertEditPermission() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(false);

        HabilitationDto result = controller.checkHabilitation(UserRoles.EXPERT, questioningId, authentication);

        assertThat(result.isHabilitated()).isFalse();
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for unsupported role")
    void shouldThrowForUnsupportedRole() {
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        controller.checkHabilitation("UNKNOWN", questioningId, authentication)
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Permission does not exist");

        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("checkPermission should return 200 when permissionEvaluatorHandler grants permission")
    void checkPermission_shouldReturn200_whenGranted() {
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        UUID questioningId = UUID.randomUUID();
        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);

        ResponseEntity<Void> response = controller.checkPermission(questioningId, Permission.INTERROGATION_DATA_READ, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ);
        verifyNoInteractions(checkHabilitationService);
    }

    @Test
    @DisplayName("checkPermission should return 403 when permissionEvaluatorHandler denies permission")
    void checkPermission_shouldReturn403_whenDenied() {
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        UUID questioningId = UUID.randomUUID();
        when(permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ))
                .thenReturn(false);

        ResponseEntity<Void> response = controller.checkPermission(questioningId, Permission.INTERROGATION_DATA_READ, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(permissionEvaluatorHandler).hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_READ);
        verifyNoInteractions(checkHabilitationService);
    }
}
