package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.global;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportAccessPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker roleChecker;

    @Mock
    Authentication authentication;

    SupportAccessPermissionEvaluator evaluator;

    @Test
    void shouldReturnReadSupportAsPermission() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);

        Assertions.assertThat(evaluator.permission())
                .isEqualTo(Permission.READ_SUPPORT);
    }

    @Test
    void shouldReturnVoidAsTargetType() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);

        Assertions.assertThat(evaluator.targetType())
                .isEqualTo(Void.class);
    }

    @Test
    void shouldReturnTrueWhenGlobalPermissionCheckerReturnsTrue() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);

        when(roleChecker.hasPermission(authentication, Permission.READ_SUPPORT))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isTrue();

        verify(roleChecker)
                .hasPermission(authentication, Permission.READ_SUPPORT);
    }

    @Test
    void shouldReturnFalseWhenGlobalPermissionCheckerReturnsFalse() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);

        when(roleChecker.hasPermission(authentication, Permission.READ_SUPPORT))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isFalse();

        verify(roleChecker)
                .hasPermission(authentication, Permission.READ_SUPPORT);
    }
}
