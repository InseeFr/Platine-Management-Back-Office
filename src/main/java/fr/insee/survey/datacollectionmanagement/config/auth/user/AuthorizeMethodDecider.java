package fr.insee.survey.datacollectionmanagement.config.auth.user;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("AuthorizeMethodDecider")
@Slf4j
@RequiredArgsConstructor
public class AuthorizeMethodDecider {

    public static final String ROLE_OFFLINE_ACCESS = "ROLE_offline_access";
    public static final String ROLE_UMA_AUTHORIZATION = "ROLE_uma_authorization";

    private final ApplicationConfig config;


    public Authentication getUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isInternalUser() {
        return hasRole(config.getRoleInternalUser());
    }

    public boolean isAdmin()  {
        return hasRole(config.getRoleAdmin());
    }

    public boolean isWebClient() {
        return hasRole(config.getRoleWebClient());
    }

    public boolean isRespondent() {
        return hasRole(config.getRoleRespondent());
    }

    private boolean hasRole(List<String> authorizedRoles) {
        return getUser().getAuthorities().stream().anyMatch(authorizedRoles::contains);
    }

    public String getUsername() {
        return getUser().getName();
    }
}
