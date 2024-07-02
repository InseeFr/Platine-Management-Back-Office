package fr.insee.survey.datacollectionmanagement.config;

import fr.insee.survey.datacollectionmanagement.config.auth.user.AuthUser;
import fr.insee.survey.datacollectionmanagement.config.auth.user.UserProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
public class TestUserProvider implements UserProvider {

    private String username;
    private List<String> roles;

    @Override
    public AuthUser getUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new AuthUser(username, authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
    }
}
