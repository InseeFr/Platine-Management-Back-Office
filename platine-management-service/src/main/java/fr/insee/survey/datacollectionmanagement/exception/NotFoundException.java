package fr.insee.survey.datacollectionmanagement.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}