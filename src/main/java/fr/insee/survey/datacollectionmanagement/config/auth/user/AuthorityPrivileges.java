package fr.insee.survey.datacollectionmanagement.config.auth.user;

public class AuthorityPrivileges {
    private AuthorityPrivileges() {
        throw new IllegalArgumentException("Constant class");
    }

    public static final String HAS_MANAGEMENT_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_REPONDENT_PRIVILEGES = "hasAnyRole('RESPONDENT')";
    public static final String HAS_REPONDENT_LIMITATED_PRIVILEGES = "hasRole('RESPONDENT') && #id == authentication.name ";
    public static final String HAS_ADMIN_PRIVILEGES = "hasAnyRole('WEB_CLIENT', 'ADMIN)";
    public static final String HAS_USER_PRIVILEGES = "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'RESPONDENT', 'ADMIN')";


}