package fr.insee.survey.datacollectionmanagement.configuration.auth.user;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_MANAGEMENT_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_REPONDENT_PRIVILEGES = "hasRole('RESPONDENT')";
    public static final String HAS_REPONDENT_LIMITATED_PRIVILEGES = "hasRole('RESPONDENT') && #id.toLowerCase() == authentication.name.toLowerCase() ";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('WEB_CLIENT', 'ADMIN)";
    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'RESPONDENT', 'ADMIN')";


}