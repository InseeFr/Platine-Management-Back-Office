package fr.insee.survey.datacollectionmanagement.questioning.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortByValidatorTest {

    final SortByValidator validator = new SortByValidator();

    @Test
    void validValues_shouldPass() {
        assertTrue(validator.isValid("score",   null));
        assertTrue(validator.isValid(null, null));

        assertTrue(validator.isValid("last_communication_type",   null));
    }

    @Test
    void invalidValues_shouldFail() {
        assertFalse(validator.isValid("",          null));
        assertFalse(validator.isValid("1=1",  null));
    }
}