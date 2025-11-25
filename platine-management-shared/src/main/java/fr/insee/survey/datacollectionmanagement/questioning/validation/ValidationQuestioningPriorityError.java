package fr.insee.survey.datacollectionmanagement.questioning.validation;

import fr.insee.survey.datacollectionmanagement.questioning.enums.ValidationQuestioningPriorityErrorType;

import java.util.List;
import java.util.UUID;

public record ValidationQuestioningPriorityError (
        ValidationQuestioningPriorityErrorType type,
        List<Integer> lines,
        UUID interrogationId) {

    public ValidationQuestioningPriorityError(
            ValidationQuestioningPriorityErrorType type,
            int line,
            UUID interrogationId
    ) {
        this(type, List.of(line), interrogationId);
    }

    public ValidationQuestioningPriorityError(
            ValidationQuestioningPriorityErrorType type,
            int line
    ) {
        this(type, List.of(line), null);
    }

    @Override
    public String toString() {

        String baseMessage = switch (type) {
            case NULL_INTERROGATION_ID ->
                    "Interrogation id cannot be null";
            case DUPLICATE_INTERROGATION_ID ->
                    String.format("Duplicate interrogation id %s is not allowed", interrogationId);
            case UNKNOWN_INTERROGATION_ID ->
                    String.format("Unknown interrogation id %s", interrogationId);
        };

        return String.format("Record %s: %s", lines, baseMessage);
    }


}
