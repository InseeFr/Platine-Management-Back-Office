package fr.insee.survey.datacollectionmanagement.user.validator;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class WalletValidator {

    private static final Pattern VALID_TEXT = Pattern.compile("^[A-Za-z0-9_\\s\\-]+$");
    private static final String FORBIDDEN_CHARACTERS = "Parameter contain forbidden special characters";

    public List<ValidationWalletError> getWalletInputErrors(List<WalletDto> wallets) {
        if (wallets == null || wallets.isEmpty()) {
            return List.of();
        }


        List<ValidationWalletError> errors = new ArrayList<>();
        checkDuplicateWallet(wallets, errors);
        for (int i = 0; i < wallets.size(); i++) {
            validateSingleWallet(wallets.get(i), i + 1, errors);
        }

        return errors;
    }

    private void checkDuplicateWallet(List<WalletDto> wallets, List<ValidationWalletError> errors) {
        Map<WalletDto, List<Integer>> lines = new LinkedHashMap<>();
        for (int i = 0; i < wallets.size(); i++) {
            WalletDto wallet = wallets.get(i);
            int lineNumber = i + 1;

            lines.computeIfAbsent(wallet, k -> new ArrayList<>())
                    .add(lineNumber);
        }

        lines.entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(e -> {
                    WalletDto wallet = e.getKey();
                    List<Integer> lineNumbers = e.getValue();
                    errors.add(new ValidationWalletError(buildDuplicateMessage(wallet, lineNumbers)));
                });
    }

    private String buildDuplicateMessage(WalletDto wallet, List<Integer> lineNumbers) {
        String lineNumbersStr = lineNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String walletStr = String.format(
                "(id_su=%s, idep=%s, id_group=%s)",
                wallet.surveyUnit(),
                wallet.internalUser(),
                wallet.group()
        );
        return String.format("Duplicate wallet %s on lines %s", walletStr, lineNumbersStr);
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
