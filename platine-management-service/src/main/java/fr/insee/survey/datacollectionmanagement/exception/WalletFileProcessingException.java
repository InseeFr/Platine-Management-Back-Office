package fr.insee.survey.datacollectionmanagement.exception;

/**
 * Exception levée lorsqu'une erreur de lecture ou de parsing de fichier Wallet survient.
 */
public class WalletFileProcessingException extends RuntimeException {
  public WalletFileProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
