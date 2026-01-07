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
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                spy(new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler));

        HabilitationDto expected = new HabilitationDto(true);
        doReturn(expected).when(controller)
                .checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);

        // When
        HabilitationDto result = controller.checkHabilitation(null, questioningId, authentication);

        // Then
        assertThat(result).isSameAs(expected);
        verify(controller).checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);
        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("should use INTERROGATION_DATA_EDIT when role is blank")
    void shouldUseEditPermissionWhenRoleIsBlank() {
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                spy(new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler));

        HabilitationDto expected = new HabilitationDto(false);
        doReturn(expected).when(controller)
                .checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);

        // When
        HabilitationDto result = controller.checkHabilitation("   ", questioningId, authentication);

        // Then
        assertThat(result).isSameAs(expected);
        verify(controller).checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);
        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("should map REVIEWER role to INTERROGATION_DATA_READ")
    void shouldMapReviewerToReadPermission() {
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                spy(new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler));

        HabilitationDto expected = new HabilitationDto(true);
        doReturn(expected).when(controller)
                .checkPermission(questioningId, Permission.INTERROGATION_DATA_READ, authentication);

        // When
        HabilitationDto result = controller.checkHabilitation(UserRoles.REVIEWER, questioningId, authentication);

        // Then
        assertThat(result).isSameAs(expected);
        verify(controller).checkPermission(questioningId, Permission.INTERROGATION_DATA_READ, authentication);
        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("should map INTERVIEWER role to INTERROGATION_DATA_EDIT")
    void shouldMapInterviewerToEditPermission() {
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                spy(new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler));

        HabilitationDto expected = new HabilitationDto(true);
        doReturn(expected).when(controller)
                .checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);

        // When
        HabilitationDto result = controller.checkHabilitation(UserRoles.INTERVIEWER, questioningId, authentication);

        // Then
        assertThat(result).isSameAs(expected);
        verify(controller).checkPermission(questioningId, Permission.INTERROGATION_DATA_EDIT, authentication);
        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("should map EXPERT role to INTERROGATION_EXPERT_DATA_EDIT")
    void shouldMapExpertToExpertEditPermission() {
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                spy(new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler));

        HabilitationDto expected = new HabilitationDto(false);
        doReturn(expected).when(controller)
                .checkPermission(questioningId, Permission.INTERROGATION_EXPERT_DATA_EDIT, authentication);

        // When
        HabilitationDto result = controller.checkHabilitation(UserRoles.EXPERT, questioningId, authentication);

        // Then
        assertThat(result).isSameAs(expected);
        verify(controller).checkPermission(questioningId, Permission.INTERROGATION_EXPERT_DATA_EDIT, authentication);
        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for unsupported role")
    void shouldThrowForUnsupportedRole() {
        // Given
        UUID questioningId = UUID.randomUUID();
        CheckHabilitationController controller =
                new CheckHabilitationController(checkHabilitationService, permissionEvaluatorHandler);

        // When / Then
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        controller.checkHabilitation("UNKNOWN", questioningId, authentication)
                ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Permission does not exist");

        verifyNoInteractions(checkHabilitationService, permissionEvaluatorHandler);
    }
}
