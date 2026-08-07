package com.autohub.ai.service;

import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final String SEPARATOR = "/";
    private final RestClient restClient;

    @Value("${rest-client.url.auto-hub-agency-cars}")
    private String url;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L)
    )
    public List<CarResponse> getAllAvailableCars(AuthenticationInfo authenticationInfo) {
        return restClient.get()
                .uri(url + SEPARATOR + "available")
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.isError()) {
                        throw new AutoHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    List<CarResponse> cars = clientResponse.bodyTo(new ParameterizedTypeReference<>() {
                    });

                    if (ObjectUtils.isEmpty(cars)) {
                        throw new AutoHubException("No car available at this moment");
                    }

                    return cars;
                });
    }

}
