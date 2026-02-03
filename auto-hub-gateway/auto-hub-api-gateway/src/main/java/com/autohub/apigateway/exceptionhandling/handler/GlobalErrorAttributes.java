package com.autohub.apigateway.exceptionhandling.handler;

import com.autohub.apigateway.exceptionhandling.exception.AutoHubResponseStatusException;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    public static final String UNEXPECTED_ERROR = "Unexpected error";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = super.getError(request);
        String message = getMessage(errorAttributes, error);

        errorAttributes.put(MESSAGE, message);

        return errorAttributes;
    }

    private String getMessage(Map<String, Object> errorAttributes, Throwable error) {
        String message = error.getMessage();

        if (HttpStatus.INTERNAL_SERVER_ERROR.value() == (Integer) errorAttributes.get(STATUS) && message.length() > 1000) {
            return UNEXPECTED_ERROR;
        }

        if (error instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException.getReason();
        }

        if (error instanceof WebClientResponseException webClientResponseException) {
            return webClientResponseException.getStatusText();
        }

        return message;
    }

}
