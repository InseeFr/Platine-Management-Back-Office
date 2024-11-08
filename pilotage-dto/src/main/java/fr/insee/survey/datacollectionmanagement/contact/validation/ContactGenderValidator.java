package fr.insee.survey.datacollectionmanagement.contact.validation;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
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
        return Arrays.stream(Contact.Gender.values()).anyMatch(v -> value.equals(v.name()));
    }
}