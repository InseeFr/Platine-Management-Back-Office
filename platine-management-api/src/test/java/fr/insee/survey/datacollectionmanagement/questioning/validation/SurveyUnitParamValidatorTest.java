package fr.insee.survey.datacollectionmanagement.questioning.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurveyUnitParamValidatorTest {

    private final SurveyUnitParamValidator validator = new SurveyUnitParamValidator();

    @Test
    void validValues_shouldPass() {
        assertTrue(validator.isValid("id",   null));
        assertTrue(validator.isValid("code", null));
        assertTrue(validator.isValid("name", null));

        // insensibilité à la casse
        assertTrue(validator.isValid("ID",   null));
        assertTrue(validator.isValid("Code", null));
        assertTrue(validator.isValid("NaMe", null));
    }

    @Test
    void invalidValues_shouldFail() {
        assertFalse(validator.isValid(null,        null));
        assertFalse(validator.isValid("",          null));
        assertFalse(validator.isValid("identifier",null));
        assertFalse(validator.isValid("whatever",  null));
    }
}
