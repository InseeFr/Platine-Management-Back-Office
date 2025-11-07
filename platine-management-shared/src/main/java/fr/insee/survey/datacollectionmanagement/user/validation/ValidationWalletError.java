package fr.insee.survey.datacollectionmanagement.user.validation;

public record ValidationWalletError(Integer line, String field, String message) {

    public ValidationWalletError(String field, String message) {
        this(null, field, message);
    }

    @Override
    public String toString() {
        if (line == null) {
            return String.format("Record: on field %s, %s%n", field, message);
        }
        return String.format("Record %d: on field %s, %s%n", line, field, message);
    }
}
