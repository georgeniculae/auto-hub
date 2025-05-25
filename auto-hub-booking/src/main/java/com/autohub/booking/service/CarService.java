package com.autohub.booking.service;

import com.autohub.booking.util.Constants;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final RestClient restClient;

    @Value("${rest-client.url.auto-hub-agency-cars}")
    private String url;

    public AvailableCarInfo findAvailableCarById(AuthenticationInfo authenticationInfo, Long carId) {
        String finalUrl = url + Constants.SEPARATOR + carId + Constants.SEPARATOR + "availability";

        return restClient.get()
                .uri(finalUrl)
                .headers(HttpRequestUtil.setHttpHeaders(authenticationInfo.apikey(), authenticationInfo.roles()))
                .exchange((_, clientResponse) -> {
                    HttpStatusCode statusCode = clientResponse.getStatusCode();

                    if (statusCode.isError()) {
                        throw new AutoHubResponseStatusException(statusCode, clientResponse.getStatusText());
                    }

                    return Optional.ofNullable(clientResponse.bodyTo(AvailableCarInfo.class))
                            .orElseThrow(() -> new AutoHubNotFoundException("Car with id: " + carId + " not found"));
                });
    }

}
