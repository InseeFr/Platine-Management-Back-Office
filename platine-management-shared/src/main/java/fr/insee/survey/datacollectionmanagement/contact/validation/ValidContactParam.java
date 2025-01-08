package fr.insee.survey.datacollectionmanagement.contact.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ContactParamValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidContactParam {
    String message() default "Invalid searchParam value.Only identifier, email and name are valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}