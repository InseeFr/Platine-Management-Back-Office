package fr.insee.survey.datacollectionmanagement.user.validator;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import fr.insee.survey.datacollectionmanagement.user.validator.stub.WalletValidationServiceStub;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletValidatorTest {

    private final WalletValidationServiceStub dbStub = new WalletValidationServiceStub();
    private final WalletValidator validator = new WalletValidator(dbStub);

    @Test
    void returnsEmpty_whenWalletsNullOrEmpty() {
        assertTrue(validator.validate(null).isEmpty());
        assertTrue(validator.validate(Collections.emptyList()).isEmpty());
    }

    @Test
    void returnsError_whenParametersMissing() {
        WalletDto w1 = new WalletDto(null, null, null);
        List<ValidationWalletError> errors = validator.validate(List.of(w1));
        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(e -> "surveyUnit".equals(e.field())));
        assertTrue(errors.stream().anyMatch(e -> "group|internal_user".equals(e.field())));
    }

    @Test
    void returnsError_whenGroupContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-001", "AAAAAA", "bad<>group");
        List<ValidationWalletError> errors = validator.validate(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("group", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void returnsError_whenSurveyUnitContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-<>001", "AAAAAA", "G1");
        List<ValidationWalletError> errors = validator.validate(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.get(0);
        assertEquals("survey_unit", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void returnsError_whenInternalUserContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-001", "A!A@A", "G1");
        List<ValidationWalletError> errors = validator.validate(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("internal_user", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void whenBothMissing_dbAndProvidedErrorsCombined() {
        dbStub.setToReturn(List.of(
                new ValidationWalletError(null, "internal_user", "Unknown Internal Users: ZZZZZZ."),
                new ValidationWalletError(null, "survey_unit", "Unknown Survey Units: SU-ZZZ.")
        ));
        WalletDto w = new WalletDto("SU-001", "A!A@A", "G1");
        List<ValidationWalletError> errors = validator.validate(List.of(w));
        assertEquals(3, errors.size());
        assertTrue(errors.stream().anyMatch(e -> "internal_user".equals(e.field()) && e.message().equals("Parameter contain forbidden special characters")));
        assertTrue(errors.stream().anyMatch(e -> "internal_user".equals(e.field()) && e.message().contains("ZZZZZZ")));
        assertTrue(errors.stream().anyMatch(e -> "survey_unit".equals(e.field()) && e.message().contains("SU-ZZZ")));
    }

    @Test
    void returnsEmpty_whenAllValidAndDbOk() {
        WalletDto w1 = new WalletDto("SU-001", "AAAAAA", "G1");
        WalletDto w2 = new WalletDto("SU-002", "BBBBBB", null);
        List<ValidationWalletError> errors = validator.validate(List.of(w1, w2));
        assertTrue(errors.isEmpty());
    }
}