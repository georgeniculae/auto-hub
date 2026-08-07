package com.autohub.ai.consumer;

import com.autohub.ai.service.CarVectorStoreService;
import com.autohub.dto.common.UpdateCarsRequest;
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
public class UpdateCarsMessageConsumer {

    private final CarVectorStoreService carVectorStoreService;

    @Bean
    public Consumer<Message<UpdateCarsRequest>> updateCarsConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<UpdateCarsRequest> message) {
        UpdateCarsRequest payload = message.getPayload();

        carVectorStoreService.deleteCar(payload.actualCarId());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Car with id {} removed from vector store after booking update", payload.actualCarId());
    }

}
