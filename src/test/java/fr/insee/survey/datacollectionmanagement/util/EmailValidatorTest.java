package fr.insee.survey.datacollectionmanagement.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {

    @Test
    @DisplayName("Check valid email")
    void testValidMails(){

        assertTrue(EmailValidatorRegex.isValidEmail("test@cocorico.fr"));
        assertTrue(EmailValidatorRegex.isValidEmail("test59@cocorico.fr"));
        assertTrue(EmailValidatorRegex.isValidEmail("test@cocorico.test.fr"));
        assertTrue(EmailValidatorRegex.isValidEmail("test_test@cocorico.test.fr"));
        assertTrue(EmailValidatorRegex.isValidEmail("1234567890@example.com"));
        assertTrue(EmailValidatorRegex.isValidEmail("email@example-one.com"));
        assertTrue(EmailValidatorRegex.isValidEmail("email@example.museum"));
        assertTrue(EmailValidatorRegex.isValidEmail("email@example.co.jp"));
        assertTrue(EmailValidatorRegex.isValidEmail("test-test@cocorico.test.fr"));


    }

    @Test
    @DisplayName("Check invalid emails")
    void testInvalidMails(){
        assertFalse(EmailValidatorRegex.isValidEmail("testé@cocorico.fr"));
        assertFalse(EmailValidatorRegex.isValidEmail("test@@cocorico.fr"));
        assertFalse(EmailValidatorRegex.isValidEmail("email.example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("email@example@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail(".email@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("email.@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("email@.example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("email..email@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("あいうえお@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("email@example.com (Joe Smith)"));
        assertFalse(EmailValidatorRegex.isValidEmail("email@111.222.333.44444"));
        assertFalse(EmailValidatorRegex.isValidEmail("Abc..123@example.com"));
        assertFalse(EmailValidatorRegex.isValidEmail("Joe Smith <email@example.com>"));
        assertFalse(EmailValidatorRegex.isValidEmail("plainaddress"));


    }
}
