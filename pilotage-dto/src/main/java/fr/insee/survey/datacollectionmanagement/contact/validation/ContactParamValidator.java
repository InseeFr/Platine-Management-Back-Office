package fr.insee.survey.datacollectionmanagement.contact.validation;

import fr.insee.survey.datacollectionmanagement.contact.util.ContactParamEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ContactParamValidator implements ConstraintValidator<ValidContactParam, String> {

    @Override
    public void initialize(ValidContactParam constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

    }

    @Override
    public boolean isValid(String searchParam, ConstraintValidatorContext context) {
        if (searchParam == null)
            return false;
        return Arrays.stream(ContactParamEnum.values()).anyMatch(v -> searchParam.equalsIgnoreCase(v.name()));
    }
}
