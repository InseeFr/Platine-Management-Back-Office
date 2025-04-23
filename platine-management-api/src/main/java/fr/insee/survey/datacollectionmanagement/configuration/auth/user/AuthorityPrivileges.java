package fr.insee.survey.datacollectionmanagement.configuration.auth.user;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_MANAGEMENT_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_READER_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN', 'READER')";
    public static final String HAS_RESPONDENT_PRIVILEGES = "hasRole('RESPONDENT')";
    public static final String HAS_PORTAL_PRIVILEGES = "hasAnyRole('PORTAL', 'INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_RESPONDENT_LIMITED_PRIVILEGES = "hasRole('RESPONDENT') && #id.toLowerCase() == authentication.name.toLowerCase() ";
    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'RESPONDENT', 'ADMIN')";
}