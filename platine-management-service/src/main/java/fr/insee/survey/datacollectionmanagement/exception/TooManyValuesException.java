package fr.insee.survey.datacollectionmanagement.exception;

public class TooManyValuesException extends RuntimeException {
    public TooManyValuesException(String errorMessage) {
        super(errorMessage);
    }

}
