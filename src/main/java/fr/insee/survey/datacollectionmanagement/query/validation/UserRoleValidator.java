package fr.insee.survey.datacollectionmanagement.query.validation;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

    @Override
    public void initialize(ValidUserRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

    }

    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        return role == null || role.isBlank() || UserRoles.INTERVIEWER.equals(role) || UserRoles.REVIEWER.equals(role);
    }
}
