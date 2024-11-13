package fr.insee.survey.datacollectionmanagement.user.validation;

import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class InternalUserRoleValidator implements ConstraintValidator <InternalUserRoleValid, String>{


    @Override
    public void initialize(InternalUserRoleValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null)
            return false;
        return Arrays.stream(UserRoleTypeEnum.values()).anyMatch(v -> value.equalsIgnoreCase(v.name()));
    }
}
