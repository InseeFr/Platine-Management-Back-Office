package fr.insee.survey.datacollectionmanagement.exception;

/**
 * Exception raised when a Wallet file reading or parsing error occurs.
 */
public class CsvFileProcessingException extends RuntimeException {
    public CsvFileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvFileProcessingException(String message) {
        super(message);
    }
}
