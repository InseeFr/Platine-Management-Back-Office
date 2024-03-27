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
    private AuthUser noAuthUser;

    private final  UserProvider userProvider;

    private final ApplicationConfig config;


    public AuthUser getUser() {
        if (config.getAuthType().equals(AuthConstants.OIDC)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AuthUser currentAuthUser = userProvider.getUser(authentication);
            return currentAuthUser;
        }
        return noAuthUser();
    }

    private AuthUser noAuthUser() {
        if (this.noAuthUser != null) {
            return this.noAuthUser;
        }

        List<String> roles = new ArrayList<>();
        roles.add(ROLE_OFFLINE_ACCESS);
        roles.add(config.getRoleAdmin().get(0));
        roles.add(ROLE_UMA_AUTHORIZATION);
        return new AuthUser("GUEST", roles);
    }

    public boolean isInternalUser() {
        AuthUser authUser = getUser();
        return isInternalUser(authUser);
    }

    public boolean isInternalUser(AuthUser authUser) {
        return (hasRole(authUser, config.getRoleInternalUser()));
    }

    public boolean isAdmin()  {
        AuthUser authUser = getUser();
        return isAdmin(authUser);
    }

    public boolean isAdmin(AuthUser authUser) {
        return (hasRole(authUser, config.getRoleAdmin()));
    }

    public boolean isWebClient() {
        AuthUser authUser = getUser();
        return isWebClient(authUser);
    }

    public boolean isWebClient(AuthUser authUser) {
        return hasRole(authUser, config.getRoleWebClient());
    }

    public boolean isRespondent() {
        AuthUser authUser = getUser();
        return isRespondent(authUser);
    }

    public boolean isRespondent(AuthUser authUser) {

        return hasRole(authUser, config.getRoleRespondent());
    }

    private boolean hasRole(AuthUser authUser, List<String> authorizedRoles) {
        Boolean hasRole = false;
        List<String> userRoles = authUser.getRoles();
        return userRoles.stream().anyMatch(authorizedRoles::contains);
    }

    public String getUsername() {
        AuthUser authUser = getUser();
        return authUser.getId().toUpperCase();
    }

}
