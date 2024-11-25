package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.SurveyUnitParamEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class SurveyUnitParamValidator implements ConstraintValidator<ValidSurveyUnitParam, String> {

    @Override
    public void initialize(ValidSurveyUnitParam constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

    }

    @Override
    public boolean isValid(String searchParam, ConstraintValidatorContext context) {
        if (searchParam == null)
            return false;
        return Arrays.stream(SurveyUnitParamEnum.values()).anyMatch(v -> searchParam.equalsIgnoreCase(v.name()));
    }
}
