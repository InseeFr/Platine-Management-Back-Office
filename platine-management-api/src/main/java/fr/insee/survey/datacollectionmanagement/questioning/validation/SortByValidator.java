package fr.insee.survey.datacollectionmanagement.questioning.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SortByValidator implements ConstraintValidator<SortByValid, String> {
    @Override
    public void initialize(SortByValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String sortBy, ConstraintValidatorContext cxt) {
        if (sortBy == null) {
            return true;
        }
        return Pattern.matches("[A-Za-z0-9_\\-]+", sortBy);
    }
}
