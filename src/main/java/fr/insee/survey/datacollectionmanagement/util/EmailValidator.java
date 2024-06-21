package fr.insee.survey.datacollectionmanagement.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isValidEmail(String email) {
        // Compiler l'expression régulière
        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        // Associer l'expression régulière à l'adresse email
        Matcher matcher = pattern.matcher(email);

        // Retourner si l'adresse email correspond à l'expression régulière
        return matcher.matches();
    }
}
