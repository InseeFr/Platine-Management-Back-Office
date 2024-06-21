package fr.insee.survey.datacollectionmanagement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static boolean isValidEmail(String email) {
        // Compiler l'expression régulière
        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        // Associer l'expression régulière à l'adresse email
        Matcher matcher = pattern.matcher(email);

        // Retourner si l'adresse email correspond à l'expression régulière
        return matcher.matches();
    }
}
