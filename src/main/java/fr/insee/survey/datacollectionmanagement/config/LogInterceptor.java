package fr.insee.survey.datacollectionmanagement.config;

import fr.insee.survey.datacollectionmanagement.constants.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final ApplicationConfig applicationConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        String userId = null;

        switch (applicationConfig.getAuthType()) {
            case AuthConstants.OIDC:
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                ThreadContext.put("user", authentication.getName().toUpperCase());
                break;
            default:
                userId = "GUEST";
                break;
        }

        ThreadContext.put("id", fishTag);
        ThreadContext.put("path", operationPath);
        ThreadContext.put("method", method);


        log.info("[" + userId.toUpperCase() + "] - [" + method + "] - [" + operationPath + "]");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception exception) throws Exception {
        ThreadContext.clearMap();
    }
}

