package fr.insee.survey.datacollectionmanagement.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class WalletBusinessRuleException extends RuntimeException {

    private final List<String> errors;

    public WalletBusinessRuleException(String message,  List<String> errors) {
        super(message);
        this.errors = errors;
    }

}
