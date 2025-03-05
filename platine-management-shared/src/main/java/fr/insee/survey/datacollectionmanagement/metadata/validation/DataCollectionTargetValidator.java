package fr.insee.survey.datacollectionmanagement.metadata.validation;

import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class DataCollectionTargetValidator implements ConstraintValidator<DataCollectionTargetValid, String> {


    @Override
    public void initialize(DataCollectionTargetValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        return Arrays.stream(DataCollectionEnum.values()).anyMatch(v -> value.equals(v.name()));
    }
}