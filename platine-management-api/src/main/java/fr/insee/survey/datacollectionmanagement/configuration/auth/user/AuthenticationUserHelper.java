package fr.insee.survey.datacollectionmanagement.configuration.auth.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUserHelper {

    public Authentication getCurrentUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getContactToken() {

        Authentication authentication = getAuthenticationPrincipal();
        if (authentication instanceof JwtAuthenticationToken auth) {
            return auth.getToken().getTokenValue();
        }
        throw new AuthenticationTokenException("Cannot retrieve token for the contact");
    }

    public Authentication getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
