package fr.insee.survey.datacollectionmanagement.questioning.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortDirectionValidatorTest {

    final SortDirectionValidator validator = new SortDirectionValidator();

    @Test
    void validValues_shouldPass() {
        assertTrue(validator.isValid("asc",   null));
        assertTrue(validator.isValid("desc", null));
        assertTrue(validator.isValid(null, null));

        assertTrue(validator.isValid("ASC",   null));
        assertTrue(validator.isValid("DESC", null));
    }

    @Test
    void invalidValues_shouldFail() {
        assertFalse(validator.isValid("",          null));
        assertFalse(validator.isValid("whatever",  null));
    }
}