package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.global;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportAccessPermissionEvaluatorTest {

    @Mock
    GlobalPermissionChecker roleChecker;

    @Mock
    ProfiledAuthenticationToken authentication;

    SupportAccessPermissionEvaluator evaluator;

    @Test
    void shouldReturnReadSupportAsPermission() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);

        Assertions.assertThat(evaluator.permission())
                .isEqualTo(Permission.SUPPORT_READ);
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
        when(authentication.getName()).thenReturn("user");
        when(roleChecker.hasPermission(authentication, Permission.SUPPORT_READ))
                .thenReturn(true);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isTrue();

        verify(roleChecker)
                .hasPermission(authentication, Permission.SUPPORT_READ);
    }

    @Test
    void shouldReturnFalseWhenGlobalPermissionCheckerReturnsFalse() {
        evaluator = new SupportAccessPermissionEvaluator(roleChecker);
        when(authentication.getName()).thenReturn("user");
        when(roleChecker.hasPermission(authentication, Permission.SUPPORT_READ))
                .thenReturn(false);

        boolean result = evaluator.hasPermission(authentication, null);

        assertThat(result)
                .isFalse();

        verify(roleChecker)
                .hasPermission(authentication, Permission.SUPPORT_READ);
    }
}
