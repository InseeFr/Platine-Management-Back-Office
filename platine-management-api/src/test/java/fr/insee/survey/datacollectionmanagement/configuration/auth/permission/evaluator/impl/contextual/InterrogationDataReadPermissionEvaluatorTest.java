package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.junit.jupiter.api.BeforeEach;
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
class InterrogationDataReadPermissionEvaluatorTest {

    @Mock
    InterrogationDataPermissionRules rules;

    @Mock
    Authentication authentication;

    InterrogationDataReadPermissionEvaluator evaluator;

    UUID questioningId;

    @BeforeEach
    void setUp() {
        evaluator = new InterrogationDataReadPermissionEvaluator(rules);
        questioningId = UUID.randomUUID();
    }

    @Test
    @DisplayName("should expose INTERROGATION_DATA_READ as handled permission")
    void shouldExposePermission() {
        // Given / When
        Permission permission = evaluator.permission();

        // Then
        assertThat(permission).isEqualTo(Permission.INTERROGATION_DATA_READ);
    }

    @Test
    @DisplayName("should declare UUID as target type")
    void shouldDeclareTargetType() {
        // Given / When
        Class<UUID> type = evaluator.targetType();

        // Then
        assertThat(type).isEqualTo(UUID.class);
    }

    @Test
    @DisplayName("should delegate evaluation to rules")
    void shouldDelegateToRules() {
        // Given
        when(rules.evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ))
                .thenReturn(true);

        // When
        boolean result = evaluator.hasPermission(authentication, questioningId);

        // Then
        assertThat(result).isTrue();
        verify(rules).evaluate(authentication, questioningId, Permission.INTERROGATION_DATA_READ);
        verifyNoMoreInteractions(rules);
    }
}
