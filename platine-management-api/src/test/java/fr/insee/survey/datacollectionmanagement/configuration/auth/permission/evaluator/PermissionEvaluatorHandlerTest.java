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
                        Permission.SUPPORT_READ, voidEvaluator,
                        Permission.INTERROGATION_DATA_EXPORT, uuidEvaluator
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
                "SUPPORT_READ"
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
                emptyHandler.hasPermission(authentication, null, Permission.SUPPORT_READ)
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class);
    }

    @Test
    void shouldThrowWhenTargetIsRequiredButMissing() {
        when(uuidEvaluator.targetType()).thenReturn(UUID.class);

        assertThatThrownBy(() ->
                handler.hasPermission(authentication, null, Permission.INTERROGATION_DATA_EXPORT)
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
                        Permission.INTERROGATION_DATA_EXPORT
                )
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class)
                .hasMessageContaining("Invalid target type");
    }

    @Test
    void shouldThrowWhenTargetTypeIsNotNullAndEvaluatorNeedsNothing() {
        when(voidEvaluator.targetType()).thenReturn(Void.class);

        assertThatThrownBy(() ->
                handler.hasPermission(
                        authentication,
                        "not-a-uuid",
                        Permission.SUPPORT_READ
                )
        )
                .isInstanceOf(ApplicationPermissionEvaluatorException.class)
                .hasMessageContaining("Permission is a global permission, do not supply an id");
    }

    @Test
    void shouldInvokeEvaluatorWithVoidTarget() {
        when(voidEvaluator.targetType()).thenReturn(Void.class);
        when(voidEvaluator.hasPermission(authentication, null)).thenReturn(true);

        boolean result = handler.hasPermission(
                authentication,
                null,
                Permission.SUPPORT_READ
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
                Permission.INTERROGATION_DATA_EXPORT
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
                Permission.INTERROGATION_DATA_EXPORT.name()
        );

        assertThat(result).isTrue();
        verify(uuidEvaluator).hasPermission(authentication, target);
    }
}
