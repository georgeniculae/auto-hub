package com.autohub.ai.consumer;

import com.autohub.ai.service.CarVectorStoreService;
import com.autohub.dto.common.CarState;
import com.autohub.dto.common.CarStatusUpdate;
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
public class CarStatusUpdateMessageConsumer {

    private final CarVectorStoreService carVectorStoreService;

    @Bean
    public Consumer<Message<CarStatusUpdate>> carStatusUpdateConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<CarStatusUpdate> message) {
        CarStatusUpdate payload = message.getPayload();

        if (CarState.NOT_AVAILABLE.equals(payload.carState())) {
            carVectorStoreService.deleteCar(payload.carId());
            log.info("Car with id {} removed from vector store after booking creation", payload.carId());
        }

        KafkaUtil.acknowledgeMessage(message.getHeaders());
    }

}
