package fr.insee.survey.datacollectionmanagement.user.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationWalletErrorTest {

    @Test
    void toString_shouldHandleMessageOnly() {
        // given
        ValidationWalletError error = new ValidationWalletError("Some error message");

        // when
        String result = error.toString();

        // then
        assertEquals("Record: Some error message", result);
    }

    @Test
    void toString_shouldHandleFieldAndMessage() {
        // given
        ValidationWalletError error = new ValidationWalletError("amount", "must be positive");

        // when
        String result = error.toString();

        // then
        assertEquals("Record: on field 'amount', must be positive", result);
    }

    @Test
    void toString_shouldHandleLineFieldAndMessage() {
        // given
        ValidationWalletError error = new ValidationWalletError(5, "amount", "must be positive");

        // when
        String result = error.toString();

        // then
        assertEquals("Record 5: on field 'amount', must be positive", result);
    }
}