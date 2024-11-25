package fr.insee.survey.datacollectionmanagement.configuration;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthenticationUserHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final AuthenticationUserHelper authenticationUserHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String fishTag = UUID.randomUUID().toString();
        String method = request.getMethod();
        String operationPath = request.getRequestURI();

        Authentication authentication = authenticationUserHelper.getCurrentUser();
        ThreadContext.put("user", authentication.getName().toUpperCase());


        ThreadContext.put("id", fishTag);
        ThreadContext.put("path", operationPath);
        ThreadContext.put("method", method);


        log.info("[" + authentication.getName().toUpperCase() + "] - [" + method + "] - [" + operationPath + "]");
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

