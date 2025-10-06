package fr.insee.survey.datacollectionmanagement.questioning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsExpertEventValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsExpertEvent {
    String message() default "type must be one of EXPERT, ONGEXPERT, VALID, ENDEXPERT, NOQUAL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
