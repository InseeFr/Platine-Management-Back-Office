package fr.insee.survey.datacollectionmanagement.metadata.util;

import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlRedirectionEnum;
import fr.insee.survey.datacollectionmanagement.questioning.util.UrlTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParamValidatorTest {

    @Test
    @DisplayName("Should not throw exception when URL_TYPE is valid")
    void testValidateParams_WithValidUrlType_ShouldNotThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("URL_TYPE");
        dto.setParamValue(UrlTypeEnum.V1.name());  // Remplacez par une valeur valide de UrlTypeEnum

        assertDoesNotThrow(() -> ParamValidator.validateParams(dto));
    }

    @Test
    @DisplayName("Should throw NotMatchException when URL_TYPE is invalid")
    void testValidateParams_WithInvalidUrlType_ShouldThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("URL_TYPE");
        dto.setParamValue("INVALID_URL_TYPE");

        assertThrows(NotMatchException.class, () -> ParamValidator.validateParams(dto));
    }

    @Test
    @DisplayName("Should not throw exception when URL_REDIRECTION is valid")
    void testValidateParams_WithValidUrlRedirection_ShouldNotThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("URL_REDIRECTION");
        dto.setParamValue(UrlRedirectionEnum.POOL1.name());

        assertDoesNotThrow(() -> ParamValidator.validateParams(dto));
    }

    @Test
    @DisplayName("Should throw NotMatchException when URL_REDIRECTION is invalid")
    void testValidateParams_WithInvalidUrlRedirection_ShouldThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("URL_REDIRECTION");
        dto.setParamValue("INVALID_URL_REDIRECTION");

        assertThrows(NotMatchException.class, () -> ParamValidator.validateParams(dto));
    }

    @Test
    @DisplayName("Should not throw exception when MAIL_ASSISTANCE has a valid email")
    void testValidateParams_WithValidEmail_ShouldNotThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("MAIL_ASSISTANCE");
        dto.setParamValue("valid.email@example.com");

        assertDoesNotThrow(() -> ParamValidator.validateParams(dto));
    }

    @Test
    @DisplayName("Should throw NotMatchException when MAIL_ASSISTANCE has an invalid email")
    void testValidateParams_WithInvalidEmail_ShouldThrowException() {
        ParamsDto dto = new ParamsDto();
        dto.setParamId("MAIL_ASSISTANCE");
        dto.setParamValue("invalid-email");

        assertThrows(NotMatchException.class, () -> ParamValidator.validateParams(dto));
    }
}