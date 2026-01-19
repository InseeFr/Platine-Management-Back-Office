package fr.insee.survey.datacollectionmanagement.exception;

public class InterrogationNotFoundException extends RuntimeException {

    public InterrogationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}