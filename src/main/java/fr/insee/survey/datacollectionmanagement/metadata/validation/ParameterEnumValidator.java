package fr.insee.survey.datacollectionmanagement.metadata.validation;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ParameterEnumValidator implements ConstraintValidator<ParameterEnumValid, String> {


    @Override
    public void initialize(ParameterEnumValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return Arrays.stream(Parameters.ParameterEnum.values()).anyMatch(v -> value.equals(v.name()));
    }
}