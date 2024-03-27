package fr.insee.survey.datacollectionmanagement.config.auth.user;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class UserToMdcFilter extends OncePerRequestFilter {

    private static final String USER = "user";

    private final UserProvider userProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentAuthUser = userProvider.getUser(authentication);
        String userId = (currentAuthUser != null && currentAuthUser.getId() != null ? currentAuthUser.getId() : "anonymous");
        MDC.put(USER, userId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(USER);
        }
    }

}
