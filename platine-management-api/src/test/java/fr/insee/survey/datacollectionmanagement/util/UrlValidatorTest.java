package fr.insee.survey.datacollectionmanagement.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlValidatorTest {

    @Test
    @DisplayName("Check valid url")
    void testValidMails(){

        assertTrue(UrlValidator.isValidUrl("http://test.fr"));
        assertTrue(UrlValidator.isValidUrl("https://test.fr"));
        assertTrue(UrlValidator.isValidUrl("http://test.test.anything"));
        assertTrue(UrlValidator.isValidUrl("http://a1234.fr"));
        assertTrue(UrlValidator.isValidUrl("https://test-questionnaire2.dev.cocorico.fr/enquete2"));
        assertTrue(UrlValidator.isValidUrl("http://test/test/1234/.fr"));

    }

    @Test
    @DisplayName("Check invalid url")
    void testInvalidMails(){

        assertFalse(UrlValidator.isValidUrl("ttp://test.fr"));
        assertFalse(UrlValidator.isValidUrl("http:// test.fr"));
        assertFalse(UrlValidator.isValidUrl("uttps://test.fr"));


    }
}
