package fr.insee.survey.datacollectionmanagement.contact.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.AuthorizationProfile;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.ProfiledAuthenticationToken;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "0 - Permissions")
@Slf4j
@RequiredArgsConstructor
public class PermissionController {

    @Operation(summary = "Retrieve interrogation")
    @GetMapping(value = "/api/interrogations/{interroId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#interroId, 'READ_INTERRO')")
    public void plip(@PathVariable("interroId") String interroId) {
        log.info("permission success for READ_INTERRO and id: {} !!!!!", interroId);
        // ...
    }

    @Operation(summary = "Retrieve user roles")
    @GetMapping(value = "/api/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public AuthorizationProfile ploup(Authentication authentication) {
        if(authentication instanceof ProfiledAuthenticationToken token) {
            return token.getProfile();
        }
        return AuthorizationProfile.emptyAuthorizationProfile();
    }
}
