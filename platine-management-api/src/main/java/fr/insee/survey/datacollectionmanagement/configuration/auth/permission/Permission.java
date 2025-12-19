package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;

import java.util.Collections;
import java.util.Set;

public enum Permission {

    READ_SUPPORT(
            true,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.SUPPORT
    ),

    INTERROGATION_EXPORT_PDF_DATA(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.INTERNAL_USER
    ),

    INTERROGATION_ACCESS_IN_PAPER_MODE(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.INTERNAL_USER
    );

    /**
     * is this permission global (based only on roles) and exposable when retrieving current user info
     */
    private final boolean global;
    private final Set<AuthorityRoleEnum> allowedRoles;

    Permission(boolean global, AuthorityRoleEnum... allowedRoles) {
        this.global = global;
        this.allowedRoles = Set.of(allowedRoles);
    }

    public boolean global() {
        return global;
    }

    public Set<AuthorityRoleEnum> allowedRoles() {
        return allowedRoles;
    }

    public boolean isAllowedForRoles(Set<AuthorityRoleEnum> roles) {
        return !Collections.disjoint(this.allowedRoles, roles);
    }
}

