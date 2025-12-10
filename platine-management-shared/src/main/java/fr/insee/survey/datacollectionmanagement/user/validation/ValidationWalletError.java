package fr.insee.survey.datacollectionmanagement.user.validation;

public record ValidationWalletError(Integer line, String field, String message) {

    public ValidationWalletError(String field, String message) {
        this(null, field, message);
    }

    public ValidationWalletError(String message) {
        this(null, null, message);
    }

    @Override
    public String toString() {
        if (line == null && field == null) {
            return String.format("Record: %s", message);
        }
        if (line == null) {
            return String.format("Record: on field '%s', %s", field, message);
        }
        return String.format("Record %d: on field '%s', %s", line, field, message);
    }
}
