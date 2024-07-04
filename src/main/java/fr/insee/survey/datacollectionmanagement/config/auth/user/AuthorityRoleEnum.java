package fr.insee.survey.datacollectionmanagement.config.auth.user;

public enum AuthorityRoleEnum {

    ADMIN,
    WEB_CLIENT,
    INTERNAL_USER,
    RESPONDENT;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() {
        return ROLE_PREFIX + this.name();
    }

}
