package fr.insee.survey.datacollectionmanagement.user.validator;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletValidatorTest {

    private final WalletValidator validator = new WalletValidator();

    @Test
    void returnsEmpty_whenWalletsNullOrEmpty() {
        assertTrue(validator.getWalletInputErrors(null).isEmpty());
        assertTrue(validator.getWalletInputErrors(Collections.emptyList()).isEmpty());
    }

    @Test
    void returnsError_whenParametersMissing() {
        WalletDto w1 = new WalletDto(null, null, null);
        List<ValidationWalletError> errors = validator.getWalletInputErrors(List.of(w1));
        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(e -> "id_su".equals(e.field())));
        assertTrue(errors.stream().anyMatch(e -> "id_group|idep".equals(e.field())));
    }

    @Test
    void returnsError_whenGroupContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-001", "AAAAAA", "bad<>group");
        List<ValidationWalletError> errors = validator.getWalletInputErrors(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("id_group", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void returnsError_whenSurveyUnitContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-<>001", "AAAAAA", "G1");
        List<ValidationWalletError> errors = validator.getWalletInputErrors(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.get(0);
        assertEquals("id_su", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void returnsError_whenInternalUserContainsForbiddenChars() {
        WalletDto w = new WalletDto("SU-001", "A!A@A", "G1");
        List<ValidationWalletError> errors = validator.getWalletInputErrors(List.of(w));
        assertEquals(1, errors.size());
        ValidationWalletError e = errors.getFirst();
        assertEquals("idep", e.field());
        assertEquals("Parameter contain forbidden special characters", e.message());
    }

    @Test
    void returnsEmpty_whenAllValid() {
        WalletDto w1 = new WalletDto("SU-001", "AAAAAA", "G1");
        WalletDto w2 = new WalletDto("SU-002", "BBBBBB", null);
        List<ValidationWalletError> errors = validator.getWalletInputErrors(List.of(w1, w2));
        assertTrue(errors.isEmpty());
    }
}