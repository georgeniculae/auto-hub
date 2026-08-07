package com.autohub.ai.consumer;

import com.autohub.ai.service.CarVectorStoreService;
import com.autohub.dto.common.CarState;
import com.autohub.dto.common.CarUpdateDetails;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CarUpdateDetailsMessageConsumer {

    private final CarVectorStoreService carVectorStoreService;

    @Bean
    public Consumer<Message<CarUpdateDetails>> carUpdateDetailsConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<CarUpdateDetails> message) {
        CarUpdateDetails payload = message.getPayload();

        if (CarState.NOT_AVAILABLE.equals(payload.carState())) {
            carVectorStoreService.deleteCar(payload.carId());
            log.info("Car with id {} removed from vector store after booking closed", payload.carId());
        }

        KafkaUtil.acknowledgeMessage(message.getHeaders());
    }

}
