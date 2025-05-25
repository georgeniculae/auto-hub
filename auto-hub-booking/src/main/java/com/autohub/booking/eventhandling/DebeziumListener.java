package com.autohub.booking.eventhandling;

import com.autohub.booking.entity.Booking;
import com.autohub.booking.util.Constants;
import com.autohub.exception.AutoHubException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.text.CaseUtils;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static io.debezium.data.Envelope.Operation;

@Component
@Slf4j
public class DebeziumListener implements RetryListener {

    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final ObjectMapper objectMapper;
    private final CreatedBookingProcessorService createdBookingProcessorService;
    private final UpdatedBookingProcessorService updatedBookingProcessorService;
    private final DeletedBookingProcessorService deletedBookingProcessorService;

    public DebeziumListener(Configuration connectorConfiguration,
                            ObjectMapper objectMapper,
                            CreatedBookingProcessorService createdBookingProcessorService,
                            UpdatedBookingProcessorService updatedBookingProcessorService,
                            DeletedBookingProcessorService deletedBookingProcessorService) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(connectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.objectMapper = objectMapper;
        this.createdBookingProcessorService = createdBookingProcessorService;
        this.updatedBookingProcessorService = updatedBookingProcessorService;
        this.deletedBookingProcessorService = deletedBookingProcessorService;
    }

    @PostConstruct
    private void start() {
        executorService.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() {
        if (Objects.nonNull(debeziumEngine)) {
            try {
                debeziumEngine.close();
            } catch (IOException e) {
                throw new AutoHubException(e.getMessage());
            }
        }
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        log.info("Key = '{}', value = '{}'", sourceRecord.key(), sourceRecord.value());

        if (ObjectUtils.isNotEmpty(sourceRecordChangeValue)) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(Envelope.FieldName.OPERATION));

            if (Operation.READ != operation) {
                Map<String, Object> payload = getPayload(operation, sourceRecordChangeValue);
                handleBookingSending(payload, operation);

                log.info("Processed payload: {} with operation: {}", payload, operation.name());
            }
        }
    }

    private Map<String, Object> getPayload(Operation operation, Struct sourceRecordChangeValue) {
        String record = Operation.DELETE == operation ? Envelope.FieldName.BEFORE : Envelope.FieldName.AFTER;
        Struct struct = (Struct) sourceRecordChangeValue.get(record);

        return struct.schema()
                .fields()
                .stream()
                .map(Field::name)
                .filter(fieldName -> ObjectUtils.isNotEmpty(struct.get(fieldName)))
                .collect(Collectors.toMap(this::getCamelCaseFieldName, struct::get));
    }

    private void handleBookingSending(Map<String, Object> payload, Operation operation) {
        Booking booking = objectMapper.convertValue(payload, Booking.class);

        if (isCreated(operation)) {
            createdBookingProcessorService.handleBookingCreation(booking);

            return;
        }

        if (isUpdated(operation)) {
            updatedBookingProcessorService.handleBookingUpdate(booking);

            return;
        }

        if (isDeleted(operation)) {
            deletedBookingProcessorService.handleBookingDeletion(booking.getId());
        }
    }

    private String getCamelCaseFieldName(String fieldName) {
        if (fieldName.contains(Constants.UNDERSCORE)) {
            return CaseUtils.toCamelCase(fieldName, false, Constants.UNDERSCORE_CHAR);
        }

        return fieldName;
    }

    private boolean isCreated(Operation operation) {
        return Operation.CREATE.equals(operation);
    }

    private boolean isUpdated(Operation operation) {
        return Operation.UPDATE.equals(operation);
    }

    private boolean isDeleted(Operation operation) {
        return Operation.DELETE.equals(operation);
    }

}
