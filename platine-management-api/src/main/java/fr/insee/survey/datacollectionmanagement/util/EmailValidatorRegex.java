package fr.insee.survey.datacollectionmanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class EmailValidatorRegex {
    private static final String EMAIL_REGEX = "^([A-Za-z0-9_-]{1,64})(\\.[A-Za-z0-9_-]+)*+(@)([A-Za-z0-9])([A-Za-z0-9-]+\\.)+([A-Za-z]{2,})$"; //NOSONAR

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
