package fr.insee.survey.datacollectionmanagement.query.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CheckHabilitationServiceImplNoAuthTest {
    private CheckHabilitationServiceImplNoAuth checkHabilitationServiceImplNoAuth;

    @BeforeEach
    void init() {
        checkHabilitationServiceImplNoAuth = new CheckHabilitationServiceImplNoAuth();
    }

    @Test
    @DisplayName("Should return true")
    void should_return_true() {
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
}
