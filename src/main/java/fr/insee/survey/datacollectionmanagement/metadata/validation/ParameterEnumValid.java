package fr.insee.survey.datacollectionmanagement.metadata.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD, PARAMETER, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ParameterEnumValidator.class)
public @interface ParameterEnumValid {
    //error message
    String message() default "Type missing or not recognized. Only URL_REDIRECTION,URL_TYPE, MAIL_ASSISTANCE are valid";

    //represents group of constraints
    Class<?>[] groups() default {};

    //represents additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
