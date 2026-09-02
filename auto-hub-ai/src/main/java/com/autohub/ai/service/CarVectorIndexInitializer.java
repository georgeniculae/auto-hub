package com.autohub.ai.service;

import com.autohub.ai.util.ApiKeyProvider;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.ai.AvailableCarDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarVectorIndexInitializer {

    private final CarService carService;
    private final CarVectorStoreService carVectorStoreService;
    private final ApiKeyProvider apiKeyProvider;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            List<CarResponse> cars = carService.getAllAvailableCars(apiKeyProvider.getAuthenticationInfo());
            List<AvailableCarDetails> availableCars = cars.stream()
                    .map(this::toAvailableCarDetails)
                    .toList();

            carVectorStoreService.addCars(availableCars);

            log.info("Vector store reinitialized with {} available cars", cars.size());
        } catch (Exception e) {
            log.error("Vector store reinitialization failed: {}", e.getMessage(), e);
        }
    }

    private AvailableCarDetails toAvailableCarDetails(CarResponse carResponse) {
        return AvailableCarDetails.builder()
                .id(carResponse.id())
                .make(carResponse.make())
                .model(carResponse.model())
                .bodyCategory(carResponse.bodyCategory())
                .yearOfProduction(carResponse.yearOfProduction())
                .color(carResponse.color())
                .mileage(carResponse.mileage())
                .amount(carResponse.amount())
                .carLocation(carResponse.carLocation())
                .build();
    }

}
