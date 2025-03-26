package fr.insee.survey.datacollectionmanagement.contact.validation;

import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ContactGenderValidator implements ConstraintValidator<ContactGenderValid, String> {


    @Override
    public void initialize(ContactGenderValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return Arrays.stream(GenderEnum.values()).anyMatch(v -> value.equalsIgnoreCase(v.name()));
    }
}