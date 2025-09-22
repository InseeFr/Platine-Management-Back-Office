package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public record AuthorizationProfile(
        Set<AuthorityRoleEnum> appRoles,
        Map<Permission, Set<String>> sourcesByPermission
) {
    public static AuthorizationProfile emptyAuthorizationProfile() {
        return new AuthorizationProfile(null, null);
    }

    public boolean can(Permission p, String sourceId) {
        Set<String> sources = sourcesByPermission.get(p);
        if(sources == null) {
            return false;
        }
        return sources.contains(sourceId);
    }

    /** Toutes les sources pour un privilège donné (copie non modifiable) */
    public Set<String> sourcesFor(Permission p) {
        if(sourcesByPermission.containsKey(p)) {
            return Collections.unmodifiableSet(sourcesByPermission.get(p));
        }
        return Set.of();
    }

    /** Tous les privilèges disponibles pour une source donnée */
    public Set<Permission> permissionsFor(String sourceId) {
        EnumSet<Permission> permissionsForSource = EnumSet.noneOf(Permission.class);
        sourcesByPermission.forEach((permission, sources) -> {
            if (sources.contains(sourceId)) {
                permissionsForSource.add(permission);
            }
        });
        return permissionsForSource;
    }

    public Set<Permission> permissions() {
        return sourcesByPermission.keySet();
    }
}
