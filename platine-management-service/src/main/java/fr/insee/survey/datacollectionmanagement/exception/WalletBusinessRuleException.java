package fr.insee.survey.datacollectionmanagement.exception;

import jakarta.validation.ValidationException;

import java.util.List;

public class WalletBusinessRuleException extends ValidationException {

    private final List<String> errors;

    public WalletBusinessRuleException(String message,  List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public WalletBusinessRuleException(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
