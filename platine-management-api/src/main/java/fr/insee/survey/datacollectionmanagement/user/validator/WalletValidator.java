package fr.insee.survey.datacollectionmanagement.user.validator;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WalletValidator {

    private final WalletValidationService walletValidationService;

    private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9_\\s\\-]+$");
    private static final String FORBIDDEN_CHARACTERS = "Parameter contain forbidden special characters";

    public List<ValidationWalletError> validate(List<WalletDto> wallets) {
        List<ValidationWalletError> errors = new ArrayList<>();
        List<ValidationWalletError> notProvided = getValidationProvided(wallets);
        if (!notProvided.isEmpty()) {
            errors.addAll(notProvided);
        }
        List<ValidationWalletError> notExisted = walletValidationService.validateDatabaseRules(wallets);
        if (!notExisted.isEmpty()) {
            errors.addAll(notExisted);
        }
        return errors;
    }

    private List<ValidationWalletError> getValidationProvided(List<WalletDto> wallets) {
        List<ValidationWalletError> errors = new ArrayList<>();

        if (wallets == null || wallets.isEmpty()) {
            return List.of();
        }

        for (int i = 0; i < wallets.size(); i++) {
            WalletDto w = wallets.get(i);
            int line = i + 1;

            if (StringUtils.isBlank(w.group()) && StringUtils.isBlank(w.internalUser())) {
                errors.add(new ValidationWalletError(
                        line,
                        "group|internal_user",
                        "At least one of the parameters must be provided."
                ));
            }

            if (StringUtils.isBlank(w.surveyUnit())) {
                errors.add(new ValidationWalletError(
                        line,
                        "surveyUnit",
                        "Parameter must be provided."
                ));
            }

            if (StringUtils.isNotBlank(w.group()) && !VALID_TEXT.matcher(w.group()).matches()) {
                    errors.add(new ValidationWalletError(
                            line,
                            "group",
                            FORBIDDEN_CHARACTERS
                    ));
                }


            if (StringUtils.isNotBlank(w.surveyUnit()) && !VALID_TEXT.matcher(w.surveyUnit()).matches()) {
                    errors.add(new ValidationWalletError(
                            line,
                            "survey_unit",
                            FORBIDDEN_CHARACTERS
                    ));
                }


            if (StringUtils.isNotBlank(w.internalUser()) && !VALID_TEXT.matcher(w.internalUser()).matches()) {
                    errors.add(new ValidationWalletError(
                            line,
                            "internal_user",
                            FORBIDDEN_CHARACTERS
                    ));
                }

        }

        return errors;
    }
}
