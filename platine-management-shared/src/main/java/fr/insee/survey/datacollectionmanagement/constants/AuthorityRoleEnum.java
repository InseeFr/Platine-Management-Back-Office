package fr.insee.survey.datacollectionmanagement.constants;
import java.util.Arrays;
import java.util.List;

public enum AuthorityRoleEnum {

    ADMIN,
    WEB_CLIENT,
    INTERNAL_USER,
    RESPONDENT,
    PORTAL,
    READER,
    SUPPORT;



    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() {
        return ROLE_PREFIX + this.name();
    }

    public static AuthorityRoleEnum fromSecurityRole(String role) {
        return Arrays.stream(values()).filter(
                        authorityRoleEnum -> authorityRoleEnum.securityRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown security role: " + role));
    }

    public static final List<String> MANAGEMENT_EXCLUDED_SECURITY_ROLES = List.of(
            RESPONDENT.securityRole(),
            PORTAL.securityRole(),
            READER.securityRole(),
            WEB_CLIENT.securityRole(),
            SUPPORT.securityRole()
    );

}
