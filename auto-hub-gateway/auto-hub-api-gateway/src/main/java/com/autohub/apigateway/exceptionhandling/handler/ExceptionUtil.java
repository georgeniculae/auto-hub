package com.autohub.apigateway.exceptionhandling.handler;

import com.autohub.apigateway.exceptionhandling.exception.AutoHubNotFoundException;
import com.autohub.apigateway.exceptionhandling.exception.AutoHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class ExceptionUtil {

    public RuntimeException handleException(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return new AutoHubResponseStatusException(
                    webClientResponseException.getStatusCode(),
                    webClientResponseException.getResponseBodyAsString()
            );
        }

        if (e instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException;
        }

        return new AutoHubResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
    }

    public static HttpStatusCode extractExceptionStatusCode(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return webClientResponseException.getStatusCode();
        }

        if (e instanceof AutoHubNotFoundException autoHubNotFoundException) {
            return autoHubNotFoundException.getStatusCode();
        }

        if (e instanceof AutoHubResponseStatusException autoHubResponseStatusException) {
            return autoHubResponseStatusException.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
