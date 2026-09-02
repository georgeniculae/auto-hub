package com.autohub.ai.consumer;

import com.autohub.ai.service.CarVectorStoreService;
import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CarAvailableMessageConsumer {

    private final CarVectorStoreService carVectorStoreService;

    @Bean
    public Consumer<Message<AvailableCarDetails>> carAvailableConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<AvailableCarDetails> message) {
        carVectorStoreService.addCars(List.of(message.getPayload()));
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Car indexed after becoming available");
    }

}
