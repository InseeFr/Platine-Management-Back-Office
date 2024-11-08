package fr.insee.survey.datacollectionmanagement.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String errorMessage) {
        super(errorMessage);
    }
}
