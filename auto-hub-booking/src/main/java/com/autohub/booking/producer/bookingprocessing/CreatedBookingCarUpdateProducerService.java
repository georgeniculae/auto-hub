package com.autohub.booking.producer.bookingprocessing;

import com.autohub.dto.common.CarStatusUpdate;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatedBookingCarUpdateProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.saveBookingCarUpdateProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public void changeCarStatus(CarStatusUpdate carStatusUpdate) {
        try {
            kafkaTemplate.send(buildMessage(carStatusUpdate, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(carStatusUpdate, result);

                            return;
                        }

                        log.error("Unable to send car status update message: {} due to : {}", carStatusUpdate, e.getMessage());
                    })
                    .join();
        } catch (Exception e) {
            throw new AutoHubException("Error while updating car status: " + carStatusUpdate.carId() + " " + e.getMessage());
        }
    }

    private Message<CarStatusUpdate> buildMessage(CarStatusUpdate carStatusUpdate, String topicName) {
        return MessageBuilder.withPayload(carStatusUpdate)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(CarStatusUpdate carStatusUpdate, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", carStatusUpdate, result.getRecordMetadata().offset());
    }

}
