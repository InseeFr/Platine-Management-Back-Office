package fr.insee.survey.datacollectionmanagement.contact.validation;

import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ContactEventTypeValidator implements ConstraintValidator<ContactEventTypeValid, String> {


    @Override
    public void initialize(ContactEventTypeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return Arrays.stream(ContactEventTypeEnum.values()).anyMatch(v -> value.equalsIgnoreCase(v.name()));
    }
}