package fr.insee.survey.datacollectionmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.Instant;
import java.util.List;


/**
 * Component used to build APIError objects
 */
@Component
public class ApiExceptionComponent {

    private final Clock clock;

    public ApiExceptionComponent(Clock clock) {
        this.clock = clock;
    }

    /**
     * @param request      origin request
     * @param status       status from exception
     * @param errorMessage error message
     * @return error object used for JSON response
     */
    public ApiError buildApiErrorObject(WebRequest request, HttpStatus status, String errorMessage) {
        String path = getPath(request);
        Instant timestamp = clock.instant();
        return new ApiError(status, path, timestamp, errorMessage);
    }

    /**
     * @param request      origin request
     * @param status       status from exception
     * @param errorMessage error message
     * @return error object used for JSON response
     */
    public ApiError buildApiErrorObject(WebRequest request, HttpStatus status, String errorMessage, List<String> errors) {
        String path = getPath(request);
        Instant timestamp = clock.instant();
        return new ApiError(status, path, timestamp, errorMessage, errors);
    }

    /**
     * @param request origin request
     * @return get path from origin request
     */
    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
