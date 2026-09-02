package com.autohub.ai.consumer;

import com.autohub.ai.service.CarVectorStoreService;
import com.autohub.dto.agency.BodyCategory;
import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.dto.common.CarState;
import com.autohub.dto.common.CarStatusUpdate;
import com.autohub.dto.common.CarUpdateDetails;
import com.autohub.dto.common.UpdateCarsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarConsumersTest {

    @InjectMocks
    private UpdateCarsMessageConsumer updateCarsMessageConsumer;

    @InjectMocks
    private CarUpdateDetailsMessageConsumer carUpdateDetailsMessageConsumer;

    @Mock
    private CarVectorStoreService carVectorStoreService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private CarAvailableMessageConsumer carAvailableMessageConsumer;

    @InjectMocks
    private CarStatusUpdateMessageConsumer carStatusUpdateMessageConsumer;

    @Test
    void carAvailableConsumerTest_addsAndAcknowledges() {
        AvailableCarDetails payload = AvailableCarDetails.builder()
                .id(1L)
                .make("Volkswagen")
                .model("Golf")
                .bodyCategory(BodyCategory.HATCHBACK)
                .yearOfProduction(2010)
                .color("black")
                .mileage(250000)
                .amount(BigDecimal.valueOf(500))
                .carLocation("Ploiesti")
                .build();

        carAvailableMessageConsumer.carAvailableConsumer().accept(getMessage(payload));

        verify(carVectorStoreService).addCars(List.of(payload));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void carStatusUpdateConsumerTest_deletesWhenNotAvailable() {
        CarStatusUpdate payload = CarStatusUpdate.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        carStatusUpdateMessageConsumer.carStatusUpdateConsumer().accept(getMessage(payload));

        verify(carVectorStoreService).deleteCar(1L);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void carStatusUpdateConsumerTest_ignoresOtherStates() {
        CarStatusUpdate payload = CarStatusUpdate.builder()
                .carId(1L)
                .carState(CarState.AVAILABLE)
                .build();

        carStatusUpdateMessageConsumer.carStatusUpdateConsumer().accept(getMessage(payload));

        verify(carVectorStoreService, never()).deleteCar(any());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void updateCarsConsumerTest_deletesNewlyBookedCar() {
        UpdateCarsRequest payload = UpdateCarsRequest.builder()
                .previousCarId(1L)
                .actualCarId(2L)
                .build();

        updateCarsMessageConsumer.updateCarsConsumer().accept(getMessage(payload));

        verify(carVectorStoreService).deleteCar(2L);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void carUpdateDetailsConsumerTest_deletesWhenNotAvailable() {
        CarUpdateDetails payload = CarUpdateDetails.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .receptionistEmployeeId(1L)
                .build();

        carUpdateDetailsMessageConsumer.carUpdateDetailsConsumer().accept(getMessage(payload));

        verify(carVectorStoreService).deleteCar(1L);
        verify(acknowledgment).acknowledge();
    }

    private <T> Message<T> getMessage(T payload) {
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();
    }

}
