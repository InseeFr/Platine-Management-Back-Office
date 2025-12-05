package fr.insee.survey.datacollectionmanagement.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class QuestioningPriorityRulesException extends RuntimeException {

    private final List<String> errors;

    public QuestioningPriorityRulesException(List<String> errors) {
        super("Questioning priority validation failed");
        this.errors = List.copyOf(errors);
    }
}
