package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.query.validation.ValidUserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@Tag(name = "5 - Cross domain")
@RequiredArgsConstructor
public class CheckHabilitationController {

    private final CheckHabilitationService checkHabilitationService;

    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @GetMapping(path = UrlConstants.API_CHECK_HABILITATION_V1, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HabilitationDto> checkHabilitationV1(
            @Valid @ValidUserRole @RequestParam(required = false) String role,
            @RequestParam String id,
            @RequestParam String campaign,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        boolean habilitated = checkHabilitationService.checkHabilitation(role, id, campaign, userRoles, authentication.getName().toUpperCase());
        return ResponseEntity.ok(new HabilitationDto(habilitated));
    }

    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @GetMapping(path = UrlConstants.API_CHECK_HABILITATION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HabilitationDto> checkHabilitation(
            @Valid @ValidUserRole @RequestParam(required = false) String role,
            @RequestParam(name = "id") UUID questioningId,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        boolean habilitated = checkHabilitationService.checkHabilitation(role, questioningId, userRoles, authentication.getName().toUpperCase());
        return ResponseEntity.ok(new HabilitationDto(habilitated));
    }

}
