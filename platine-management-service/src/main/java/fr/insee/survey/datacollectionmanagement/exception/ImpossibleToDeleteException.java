package fr.insee.survey.datacollectionmanagement.exception;

public class ImpossibleToDeleteException extends RuntimeException {

    public ImpossibleToDeleteException(String errorMessage) {
        super(errorMessage);
    }

}
