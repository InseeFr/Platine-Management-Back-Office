package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.PermissionEvaluatorHandler;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.query.dto.HabilitationDto;
import fr.insee.survey.datacollectionmanagement.query.service.CheckHabilitationService;
import fr.insee.survey.datacollectionmanagement.query.validation.ValidUserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
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
@Slf4j
public class CheckHabilitationController {

    private final CheckHabilitationService checkHabilitationService;
    private final PermissionEvaluatorHandler permissionEvaluatorHandler;

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
    public HabilitationDto checkHabilitation(
            @Valid @ValidUserRole @RequestParam(required = false) String role,
            @RequestParam(name = "id") UUID questioningId,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {

        // same as role == interviewer
        if (StringUtils.isBlank(role)) {
            boolean habilitated = permissionEvaluatorHandler.hasPermission(authentication, questioningId, Permission.INTERROGATION_DATA_EDIT);
            return new HabilitationDto(habilitated);
        }

        Permission permissionToCheck = switch (role) {
            case UserRoles.REVIEWER -> Permission.INTERROGATION_DATA_READ;
            case UserRoles.INTERVIEWER -> Permission.INTERROGATION_DATA_EDIT;
            case UserRoles.EXPERT -> Permission.INTERROGATION_EXPERT_DATA_EDIT;
            default -> throw new IllegalArgumentException("Permission does not exist");
        };

        boolean habilitated = permissionEvaluatorHandler.hasPermission(authentication, questioningId, permissionToCheck);
        return new HabilitationDto(habilitated);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = UrlConstants.API_CHECK_PERMISSION, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkPermission(
            @RequestParam(name = "id", required = false) UUID questioningId,
            @RequestParam(name = "permission") Permission permission,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {

        log.info("GET permission {} for questioning {}", permission.name(), questioningId);
        if(permissionEvaluatorHandler.hasPermission(authentication, questioningId, permission)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
