package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterrogationExpertDataEditPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    UserService userService;

    @Mock
    QuestioningService questioningService;

    @Mock
    ProfiledAuthenticationToken authentication;

    @Mock
    User user;

    InterrogationExpertDataEditPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationExpertDataEditPermissionEvaluator(
                globalPermissionChecker,
                userService,
                questioningService
        );
        questioningId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should expose INTERROGATION_DATA_ASSESS as handled permission")
    void shouldReturnInterrogationDataAssessAsPermission() {
        assertThat(evaluator.permission())
                .isEqualTo(Permission.INTERROGATION_EXPERT_DATA_EDIT);
    }

    @Test
    @DisplayName("should declare UUID as target type")
    void shouldReturnUUIDAsTargetType() {
        assertThat(evaluator.targetType())
                .isEqualTo(UUID.class);
    }

    @Test
    @DisplayName("should deny when user does not have acceptable roles")
    void shouldDenyWhenGlobalRoleIsNotValid() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verifyNoInteractions(userService, questioningService);
        verify(authentication, never()).hasRole(any());
        verify(authentication, never()).getName();
    }

    @Test
    @DisplayName("should grant when user is admin")
    void shouldGrantWhenUserIsAdmin() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isTrue();

        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verifyNoInteractions(userService, questioningService);
    }

    @Test
    @DisplayName("should deny when user does not exist")
    void shouldDenyWhenUserDoesNotExist() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(authentication.getName()).thenReturn("user");
        when(userService.findOptionalByIdentifier("USER"))
                .thenReturn(Optional.empty());

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(userService).findOptionalByIdentifier("USER");
        verifyNoInteractions(questioningService);
    }

    @Test
    @DisplayName("should grant when user exists and questioning has expertise status")
    void shouldGrantWhenQuestioningHasExpertiseStatus() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(userService.findOptionalByIdentifier("USER"))
                .thenReturn(Optional.of(user));
        when(questioningService.hasExpertiseStatus(questioningId))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isTrue();

        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(userService).findOptionalByIdentifier("USER");
        verify(questioningService).hasExpertiseStatus(questioningId);
    }

    @Test
    @DisplayName("should deny when user exists but questioning does not have expertise status")
    void shouldDenyWhenQuestioningDoesNotHaveExpertiseStatus() {
        when(globalPermissionChecker.hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT))
                .thenReturn(true);
        when(authentication.hasRole(AuthorityRoleEnum.ADMIN))
                .thenReturn(false);
        when(authentication.getName()).thenReturn("user");
        when(userService.findOptionalByIdentifier("USER"))
                .thenReturn(Optional.of(user));
        when(questioningService.hasExpertiseStatus(questioningId))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, questioningId);

        assertThat(result).isFalse();

        verify(globalPermissionChecker).hasPermission(authentication, Permission.INTERROGATION_EXPERT_DATA_EDIT);
        verify(authentication).getName();
        verify(authentication).hasRole(AuthorityRoleEnum.ADMIN);
        verify(userService).findOptionalByIdentifier("USER");
        verify(questioningService).hasExpertiseStatus(questioningId);
    }
}
