package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionEvaluatorHandlerTest {

    @Mock
    Authentication authentication;

    @Mock
    ApplicationPermissionEvaluator<Void> voidEvaluator;

    @Mock
    ApplicationPermissionEvaluator<UUID> uuidEvaluator;

    PermissionEvaluatorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PermissionEvaluatorHandler(
                Map.of(
                        Permission.READ_SUPPORT, voidEvaluator,
                        Permission.INTERROGATION_EXPORT_PDF_DATA, uuidEvaluator
                )
        );
    }

    @Test
    void shouldReturnFalseWhenPermissionIsNull() {
        boolean result = handler.hasPermission(authentication, new Object(), null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenPermissionIsNotAString() {
        boolean result = handler.hasPermission(authentication, new Object(), 42);

        assertThat(result).isFalse();
    }

    @Test
    void shouldDelegateWhenPermissionIsValidString() {
        when(voidEvaluator.targetType()).thenReturn(Void.class);
        when(voidEvaluator.hasPermission(authentication, null)).thenReturn(true);

        boolean result = handler.hasPermission(
                authentication,
                null,
                "READ_SUPPORT"
        );

        assertThat(result).isTrue();
        verify(voidEvaluator).hasPermission(authentication, null);
    }

    @Test
    void shouldReturnFalseWhenPermissionEnumIsNull() {
        boolean result = handler.hasPermission(authentication, new Object(), (Permission) null);

        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenNoEvaluatorFound() {
        PermissionEvaluatorHandler emptyHandler =
                new PermissionEvaluatorHandler(Map.of());

        assertThatThrownBy(() ->
                emptyHandler.hasPermission(authentication, null, Permission.READ_SUPPORT)
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class);
    }

    @Test
    void shouldThrowWhenTargetIsRequiredButMissing() {
        when(uuidEvaluator.targetType()).thenReturn(UUID.class);

        assertThatThrownBy(() ->
                handler.hasPermission(authentication, null, Permission.INTERROGATION_EXPORT_PDF_DATA)
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class)
                .hasMessageContaining("Target required");
    }

    @Test
    void shouldThrowWhenTargetTypeIsInvalid() {
        when(uuidEvaluator.targetType()).thenReturn(UUID.class);

        assertThatThrownBy(() ->
                handler.hasPermission(
                        authentication,
                        "not-a-uuid",
                        Permission.INTERROGATION_EXPORT_PDF_DATA
                )
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class)
                .hasMessageContaining("Invalid target type");
    }

    // ------------------------------------------------------------------
    // invokeEvaluator – happy paths
    // ------------------------------------------------------------------

    @Test
    void shouldInvokeEvaluatorWithVoidTarget() {
        when(voidEvaluator.targetType()).thenReturn(Void.class);
        when(voidEvaluator.hasPermission(authentication, null)).thenReturn(true);

        boolean result = handler.hasPermission(
                authentication,
                null,
                Permission.READ_SUPPORT
        );

        assertThat(result).isTrue();
        verify(voidEvaluator).hasPermission(authentication, null);
    }

    @Test
    void shouldInvokeEvaluatorWithValidTypedTarget() {
        UUID target = UUID.randomUUID();

        when(uuidEvaluator.targetType()).thenReturn(UUID.class);
        when(uuidEvaluator.hasPermission(authentication, target)).thenReturn(true);

        boolean result = handler.hasPermission(
                authentication,
                target,
                Permission.INTERROGATION_EXPORT_PDF_DATA
        );

        assertThat(result).isTrue();
        verify(uuidEvaluator).hasPermission(authentication, target);
    }

    @Test
    void shouldIgnoreTargetTypeAndDelegateToOtherHasPermission() {
        UUID target = UUID.randomUUID();

        when(uuidEvaluator.targetType()).thenReturn(UUID.class);
        when(uuidEvaluator.hasPermission(authentication, target)).thenReturn(true);

        boolean result = handler.hasPermission(
                authentication,
                target,
                "ANY_TYPE",
                Permission.INTERROGATION_EXPORT_PDF_DATA.name()
        );

        assertThat(result).isTrue();
        verify(uuidEvaluator).hasPermission(authentication, target);
    }
}
