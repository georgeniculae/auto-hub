package com.autohub.lib.exceptionhandling;

import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.util.Constants;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling extends DefaultErrorAttributes {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, WebRequest request) {
        HttpStatus status = getStatus(e);
        String message = getMessage(e);

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, message, e.getLocalizedMessage(), status);

        return ResponseEntity.status(status).body(errorAttributes);
    }

    @ExceptionHandler(AutoHubNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAutoHubNotFoundException(AutoHubNotFoundException e,
                                                                              WebRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        String cause = "Resource not found";
        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getReason(), cause, notFound);

        return ResponseEntity.status(notFound).body(errorAttributes);
    }

    @ExceptionHandler(AutoHubException.class)
    public ResponseEntity<Map<String, Object>> handleAutoHubException(AutoHubException e,
                                                                      WebRequest request) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, e.getMessage(), e.getMessage(), internalServerError);

        return ResponseEntity.status(internalServerError).body(errorAttributes);
    }

    @ExceptionHandler(AutoHubResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleAutoHubResponseStatusException(AutoHubResponseStatusException e,
                                                                                    WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getReason(), e.getReason(), status);

        return ResponseEntity.status(status).body(errorAttributes);
    }

    private HttpStatus getStatus(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (e instanceof ErrorResponse errorResponse) {
            status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
        }

        if (e instanceof HttpMessageNotReadableException) {
            status = HttpStatus.BAD_REQUEST;
        }

        if (e instanceof AccessDeniedException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        if (e instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND;
        }

        return status;
    }

    private String getMessage(Exception e) {
        if (e instanceof ResponseStatusException responseStatusException) {
            return responseStatusException.getReason();
        }

        return e.getMessage();
    }

    private Map<String, Object> getErrorAttributesMap(WebRequest webRequest,
                                                      String errorMessage,
                                                      String cause,
                                                      HttpStatus httpStatus) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        errorAttributes.put(Constants.MESSAGE, errorMessage);
        errorAttributes.put(Constants.STATUS, httpStatus.value());
        errorAttributes.put(Constants.ERROR, cause);

        return errorAttributes;
    }

}
