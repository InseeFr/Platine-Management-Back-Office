package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker globalPermissionChecker;

    @Mock
    Authentication authentication;

    SupportPermissionEvaluator evaluator;

    @Test
    void shouldReturnReadSupportAsPermission() {
        evaluator = new SupportPermissionEvaluator(globalPermissionChecker);

        assertThat(evaluator.permission())
                .isEqualTo(Permission.READ_SUPPORT);
    }

    @Test
    void shouldReturnVoidAsTargetType() {
        evaluator = new SupportPermissionEvaluator(globalPermissionChecker);

        assertThat(evaluator.targetType())
                .isEqualTo(Void.class);
    }

    @Test
    void shouldReturnTrueWhenGlobalPermissionCheckerReturnsTrue() {
        evaluator = new SupportPermissionEvaluator(globalPermissionChecker);

        when(globalPermissionChecker.hasPermission(authentication, Permission.READ_SUPPORT))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isTrue();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.READ_SUPPORT);
    }

    @Test
    void shouldReturnFalseWhenGlobalPermissionCheckerReturnsFalse() {
        evaluator = new SupportPermissionEvaluator(globalPermissionChecker);

        when(globalPermissionChecker.hasPermission(authentication, Permission.READ_SUPPORT))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isFalse();

        verify(globalPermissionChecker)
                .hasPermission(authentication, Permission.READ_SUPPORT);
    }
}
