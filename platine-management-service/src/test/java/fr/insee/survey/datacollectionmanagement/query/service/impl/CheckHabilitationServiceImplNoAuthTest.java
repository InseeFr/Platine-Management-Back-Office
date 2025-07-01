package fr.insee.survey.datacollectionmanagement.query.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CheckHabilitationServiceImplNoAuthTest {
    private CheckHabilitationServiceImplNoAuth checkHabilitationServiceImplNoAuth;

    @BeforeEach
    void init() {
        checkHabilitationServiceImplNoAuth = new CheckHabilitationServiceImplNoAuth();
    }

    @Test
    @DisplayName("Should return true V1")
    void should_return_true_V1() {
        //given
        List<String> userRoles = List.of();

        //when
        boolean result = checkHabilitationServiceImplNoAuth.checkHabilitation(null,
                "id-su",
                "campaign-id",
                userRoles,
                "user-id");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true")
    void should_return_true() {
        boolean result = checkHabilitationServiceImplNoAuth.checkHabilitation(null,
                UUID.randomUUID(),
                List.of(),
                "user-id");

        //then
        assertThat(result).isTrue();
    }
}
