package fr.insee.survey.datacollectionmanagement.exception;

public class NotMatchException extends RuntimeException {
    public NotMatchException(String errorMessage) {
        super(errorMessage);
    }

}
