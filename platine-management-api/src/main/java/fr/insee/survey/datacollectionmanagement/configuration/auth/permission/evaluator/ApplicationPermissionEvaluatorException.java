package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator;

public class ApplicationPermissionEvaluatorException extends RuntimeException {
    public ApplicationPermissionEvaluatorException(Object targetedObject) {
        super(String.format("Error when applying permission evaluator: '%s'", targetedObject));
    }
}
