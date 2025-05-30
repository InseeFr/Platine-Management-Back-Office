package fr.insee.survey.datacollectionmanagement.user.validation;

import fr.insee.survey.datacollectionmanagement.user.enums.UserEventTypeEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class UserEventTypeValidator implements ConstraintValidator<UserEventTypeValid, String> {


    @Override
    public void initialize(UserEventTypeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return Arrays.stream(UserEventTypeEnum.values()).anyMatch(v -> value.equalsIgnoreCase(v.name()));
    }
}