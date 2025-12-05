package fr.insee.survey.datacollectionmanagement.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    private static final String ERROR_OCCURRED_LABEL = "An error has occurred";
    private final ApiExceptionComponent errorComponent;

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @param request request initiating the exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> processException(Exception ex, HttpStatus status, WebRequest request) {
        return processException(ex, status, request, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param request              request initiating the exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> processException(Exception ex, HttpStatus status, WebRequest request, String overrideErrorMessage) {
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ApiError error = errorComponent.buildApiErrorObject(request, status, errorMessage);
        return new ResponseEntity<>(error, status);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param request              request initiating the exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @param errors               messages details
     * @return the apierror object with associated status code
     */
    private ResponseEntity<ApiError> processException(final Exception ex, final HttpStatus status,
                                                      final WebRequest request, String overrideErrorMessage, List<String> errors) {
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ApiError error = errorComponent.buildApiErrorObject(request, status, errorMessage, errors);
        log.error("Exception occurred at {}: {}", error.getPath(), errorMessage, ex);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> noHandlerFoundException(NoHandlerFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> accessDeniedException(AccessDeniedException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.FORBIDDEN, request);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {

        String defaultMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        log.error(defaultMessage, e);
        return processException(e, HttpStatus.BAD_REQUEST, request, defaultMessage);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidationException(
            HandlerMethodValidationException e,
            WebRequest request) {

        String defaultMessage = e.getAllErrors().getFirst().getDefaultMessage();
        log.error(defaultMessage, e);
        return processException(e, HttpStatus.BAD_REQUEST, request, defaultMessage);
    }


    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException e,
            WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.BAD_REQUEST, request, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        log.error(e.getMessage(), e);

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof JsonParseException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + parseException.getOriginalMessage() + " " + location;
        }
        if (rootCause instanceof JsonMappingException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        return processException(e, HttpStatus.BAD_REQUEST, request, errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> notFoundException(NotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> emptyResultDataAccessException(EmptyResultDataAccessException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(NotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> notMatchException(NotMatchException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TooManyValuesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> tooManyValuesException(TooManyValuesException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ImpossibleToDeleteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> impossibleToDeleteException(ImpossibleToDeleteException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResponseEntity<ApiError> exceptions(HttpClientErrorException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.valueOf(e.getStatusCode().value()), request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(WalletBusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleWalletBusinessRuleException(WalletBusinessRuleException e, WebRequest request) {
        return processException(e, HttpStatus.BAD_REQUEST, request, e.getMessage(), e.getErrors());
    }

    @ExceptionHandler(WalletFileProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleWalletFileProcessingException(
            WalletFileProcessingException e, WebRequest request) {
        log.error("Wallet file processing error: {}", e.getMessage(), e);
        return processException(e, HttpStatus.BAD_REQUEST, request, e.getMessage());
    }

    @ExceptionHandler(QuestioningPriorityRulesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleQuestioningPriorityRulesException(
            QuestioningPriorityRulesException e, WebRequest request) {
        return processException(e, HttpStatus.BAD_REQUEST, request, e.getMessage(), e.getErrors());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ApiError> exceptions(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<ApiError> forbiddenAccessException(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ResponseEntity<ApiError> entityExistsException(EntityExistsException e, WebRequest request) {
        log.error(e.getMessage(), e);
        return processException(e, HttpStatus.CONFLICT, request);
    }

}