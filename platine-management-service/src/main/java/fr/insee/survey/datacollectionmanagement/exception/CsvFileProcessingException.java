package fr.insee.survey.datacollectionmanagement.exception;

/**
 * Exception levée lorsqu'une erreur de lecture ou de parsing de fichier Wallet survient.
 */
public class CsvFileProcessingException extends RuntimeException {
    public CsvFileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvFileProcessingException(String message) {
        super(message);
    }
}
