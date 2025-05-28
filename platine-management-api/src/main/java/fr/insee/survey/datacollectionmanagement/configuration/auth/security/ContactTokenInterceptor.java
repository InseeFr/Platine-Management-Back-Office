package fr.insee.survey.datacollectionmanagement.configuration.auth.security;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthenticationUserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Add interceptor to restTemplate to inject tokens when oidc is enabled
 */
@RequiredArgsConstructor
public class ContactTokenInterceptor implements ClientHttpRequestInterceptor {

    private final AuthenticationUserHelper authenticationHelper;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String jwt = authenticationHelper.getContactToken();
        headers.setBearerAuth(jwt);
        return execution.execute(request, body);
    }
}