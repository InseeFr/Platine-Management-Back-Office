package fr.insee.survey.datacollectionmanagement.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailValidatorTest {

    @Test
    @DisplayName("Check valid email")
    void testValidMails(){
        assertTrue(EmailValidator.isValidEmail("test@cocorico.fr"));
        assertTrue(EmailValidator.isValidEmail("test59@cocorico.fr"));
        assertTrue(EmailValidator.isValidEmail("test@cocorico.test.fr"));
        assertTrue(EmailValidator.isValidEmail("test_test@cocorico.test.fr"));
        assertTrue(EmailValidator.isValidEmail("1234567890@example.com"));
        assertTrue(EmailValidator.isValidEmail("email@example-one.com"));
        assertTrue(EmailValidator.isValidEmail("email@example.museum"));
        assertTrue(EmailValidator.isValidEmail("email@example.co.jp"));
        assertTrue(EmailValidator.isValidEmail("test-test@cocorico.test.fr"));


    }

    @Test
    @DisplayName("Check invalid emails")
    void testInvalidMails(){
        assertFalse(EmailValidator.isValidEmail("testé@cocorico.fr"));
        assertFalse(EmailValidator.isValidEmail("email.example.com"));
        assertFalse(EmailValidator.isValidEmail("email@example@example.com"));
        assertFalse(EmailValidator.isValidEmail(".email@example.com"));
        assertFalse(EmailValidator.isValidEmail("email.@example.com"));
        assertFalse(EmailValidator.isValidEmail("email..email@example.com"));
        assertFalse(EmailValidator.isValidEmail("あいうえお@example.com"));
        assertFalse(EmailValidator.isValidEmail("email@example.com (Joe Smith)"));
        assertFalse(EmailValidator.isValidEmail("email@111.222.333.44444"));
        assertFalse(EmailValidator.isValidEmail("Abc..123@example.com"));
        assertFalse(EmailValidator.isValidEmail("Joe Smith <email@example.com>"));
        assertFalse(EmailValidator.isValidEmail("plainaddress"));


    }
}
