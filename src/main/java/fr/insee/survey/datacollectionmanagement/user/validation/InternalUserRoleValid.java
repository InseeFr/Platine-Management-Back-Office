package fr.insee.survey.datacollectionmanagement.user.validation;

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
@Constraint(validatedBy = InternalUserRoleValidator.class)
public @interface InternalUserRoleValid {
    //error message
    String message() default "Role missing or not recognized. Only RESPONSABLE, GESTIONNAIRE, ASSISTANCE are valid";

    //represents group of constraints
    Class<?>[] groups() default {};

    //represents additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
