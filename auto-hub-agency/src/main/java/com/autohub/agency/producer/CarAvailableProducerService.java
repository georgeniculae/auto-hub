package com.autohub.agency.producer;

import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarAvailableProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.carAvailableProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L)
    )
    public void sendCarAvailable(AvailableCarDetails availableCarDetails) {
        try {
            kafkaTemplate.send(buildMessage(availableCarDetails, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info(
                                    "Sent available car: {} with offset: {}",
                                    availableCarDetails.id(),
                                    result.getRecordMetadata().offset()
                            );

                            return;
                        }

                        log.error("Unable to send message: {} due to : {}", availableCarDetails, e.getMessage());
                    })
                    .join();
        } catch (Exception e) {
            throw new AutoHubException("Error sending available car: " + availableCarDetails + " " + e.getMessage());
        }
    }

    private Message<AvailableCarDetails> buildMessage(AvailableCarDetails availableCarDetails, String topicName) {
        return MessageBuilder.withPayload(availableCarDetails)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
