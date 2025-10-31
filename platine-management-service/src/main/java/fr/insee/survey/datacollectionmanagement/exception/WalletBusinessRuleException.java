package fr.insee.survey.datacollectionmanagement.exception;

/**
 * Exception levée lorsqu'une règle métier de portefeuille (wallet) est violée.
 */
public class WalletBusinessRuleException extends RuntimeException {

  public WalletBusinessRuleException(String message) {
    super(message);
  }
}
