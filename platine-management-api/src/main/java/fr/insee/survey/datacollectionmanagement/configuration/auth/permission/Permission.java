package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;

import java.util.Collections;
import java.util.Set;

public enum Permission {

    SUPPORT_READ(
            true,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.SUPPORT
    ),

    INTERROGATION_DATA_EXPORT(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.INTERNAL_USER
    ),

    INTERROGATION_PAPER_DATA_EDIT(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.INTERNAL_USER
    ),

    INTERROGATION_DATA_READ(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.RESPONDENT,
            AuthorityRoleEnum.INTERNAL_USER
    ),

    INTERROGATION_DATA_EDIT(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.RESPONDENT
    ),

    INTERROGATION_EXPERT_DATA_EDIT(
            false,
            AuthorityRoleEnum.ADMIN,
            AuthorityRoleEnum.INTERNAL_USER
    )
    ;

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

