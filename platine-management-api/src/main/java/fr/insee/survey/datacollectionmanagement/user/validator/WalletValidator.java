package fr.insee.survey.datacollectionmanagement.user.validator;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class WalletValidator {

    private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9_\\s\\-]+$");
    private static final String FORBIDDEN_CHARACTERS = "Parameter contain forbidden special characters";

    public List<ValidationWalletError> getWalletInputErrors(List<WalletDto> wallets) {
        if (wallets == null || wallets.isEmpty()) {
            return List.of();
        }

        List<ValidationWalletError> errors = new ArrayList<>();
        for (int i = 0; i < wallets.size(); i++) {
            validateSingleWallet(wallets.get(i), i + 1, errors);
        }
        return errors;
    }

    private void validateSingleWallet(WalletDto w, int line, List<ValidationWalletError> errors) {
        checkAtLeastOneProvided(w, line, errors);
        checkRequiredSurveyUnit(w, line, errors);

        validateFieldIfPresent(w.group(), line, "id_group", errors);
        validateFieldIfPresent(w.surveyUnit(), line, "id_su", errors);
        validateFieldIfPresent(w.internalUser(), line, "idep", errors);
    }

    private void checkAtLeastOneProvided(WalletDto w, int line, List<ValidationWalletError> errors) {
        if (StringUtils.isBlank(w.group()) && StringUtils.isBlank(w.internalUser())) {
            errors.add(new ValidationWalletError(
                    line,
                    "id_group|idep",
                    "At least one of the parameters must be provided."
            ));
        }
    }

    private void checkRequiredSurveyUnit(WalletDto w, int line, List<ValidationWalletError> errors) {
        if (StringUtils.isBlank(w.surveyUnit())) {
            errors.add(new ValidationWalletError(
                    line,
                    "id_su",
                    "Parameter must be provided."
            ));
        }
    }

    private void validateFieldIfPresent(String value, int line, String fieldName, List<ValidationWalletError> errors) {
        if (StringUtils.isNotBlank(value) && !isValidText(value)) {
            errors.add(new ValidationWalletError(line, fieldName, FORBIDDEN_CHARACTERS));
        }
    }

    private boolean isValidText(String s) {
        return VALID_TEXT.matcher(s).matches();
    }
}
