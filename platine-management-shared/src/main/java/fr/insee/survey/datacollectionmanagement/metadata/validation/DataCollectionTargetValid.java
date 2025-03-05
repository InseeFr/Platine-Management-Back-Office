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
@Constraint(validatedBy = DataCollectionTargetValidator.class)
public @interface DataCollectionTargetValid {
    //error message
    String message() default "DataCollectionTarget missing or not recognized. Only LUNATIC_NORMAL, LUNATIC_SENSITIVE, XFORM1, XFORM2 are valid";

    //represents group of constraints
    Class<?>[] groups() default {};

    //represents additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
