package fr.insee.survey.datacollectionmanagement.exception;

public class TooManyInterrogationsException extends RuntimeException {
    public TooManyInterrogationsException(String errorMessage) {
        super(errorMessage);
    }
}
