package fr.insee.survey.datacollectionmanagement.questioning.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortDirectionValidator implements ConstraintValidator<SortDirectionValid, String> {
    @Override
    public void initialize(SortDirectionValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String sortDirection, ConstraintValidatorContext cxt) {
        if (sortDirection == null) {
            return true;
        }
        return sortDirection.equalsIgnoreCase("asc")
                || sortDirection.equalsIgnoreCase("desc");
    }
}
