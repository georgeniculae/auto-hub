package com.autohub.autohubmcp.service;

import com.autohub.dto.agency.CarResponse;
import com.autohub.exception.AutoHubResponseStatusException;
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

    private static final String AVAILABLE_PATH = "/available";
    private final RestClient restClient;

    @Value("${rest-client.url.auto-hub-agency-cars}")
    private String url;

    @Retryable(retryFor = Exception.class)
    public List<CarResponse> getAllAvailableCars() {
        return restClient.get()
                .uri(url + AVAILABLE_PATH)
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.isError()) {
                        throw new AutoHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    List<CarResponse> cars = clientResponse.bodyTo(new ParameterizedTypeReference<>() {
                    });

                    return ObjectUtils.isEmpty(cars) ? List.of() : cars;
                });
    }

}
