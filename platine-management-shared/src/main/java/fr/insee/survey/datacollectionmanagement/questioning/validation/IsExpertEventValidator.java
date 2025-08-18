package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsExpertEventValidator implements ConstraintValidator<IsExpertEvent, TypeQuestioningEvent> {
    @Override
    public boolean isValid(TypeQuestioningEvent value, ConstraintValidatorContext context) {
        if (value == null) { return false; }
        return TypeQuestioningEvent.EXPERT_EVENTS.contains(value);
    }
}
