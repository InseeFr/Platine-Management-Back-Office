package fr.insee.survey.datacollectionmanagement.config.auth.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUserHelper {

    public Authentication getCurrentUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
