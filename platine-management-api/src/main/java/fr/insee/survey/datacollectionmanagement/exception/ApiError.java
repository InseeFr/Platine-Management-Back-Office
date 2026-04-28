package fr.insee.survey.datacollectionmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * Default API Error object returned as JSON response to client
 */
@Data
@AllArgsConstructor
public class ApiError {
    private Integer code;
    private String path;
    private String message;

        private Instant timestamp;

    private List<String> details;

    /**
     * @param status       http status for this error
     * @param path         origin request path
     * @param timestamp    timestamp of the generated error
     * @param errorMessage error message
     */
    public ApiError(HttpStatus status, String path, Instant timestamp, String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = status.getReasonPhrase();
        }
        createApiError(status.value(), path, timestamp, errorMessage ,null);
    }

    /**
     * @param status       http status for this error
     * @param path         origin request path
     * @param timestamp    timestamp of the generated error
     * @param errorMessage error message
     *
     */
    public ApiError(HttpStatus status, String path, Instant timestamp, String errorMessage,  List<String> details) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = status.getReasonPhrase();
        }
        createApiError(status.value(), path, timestamp, errorMessage, details);
    }

    private void createApiError(int code, String path, Instant timestamp, String errorMessage,  List<String> details) {
        this.code = code;
        this.path = path;
        this.message = errorMessage;
        this.timestamp = timestamp;
        this.details = details;
    }
}
