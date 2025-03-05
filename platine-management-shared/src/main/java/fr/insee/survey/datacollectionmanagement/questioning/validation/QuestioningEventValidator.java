package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class QuestioningEventValidator implements ConstraintValidator<QuestioningEventTypeValid, String> {


    @Override
    public void initialize(QuestioningEventTypeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return Arrays.stream(TypeQuestioningEvent.values()).anyMatch(v -> value.equals(v.name()));
    }
}