package fr.insee.survey.datacollectionmanagement.configuration.auth.user;

public class AuthenticationTokenException extends RuntimeException {
    public AuthenticationTokenException(String message) {
        super(message);
    }
}
