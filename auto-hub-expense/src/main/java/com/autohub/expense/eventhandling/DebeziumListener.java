package com.autohub.expense.eventhandling;

import com.autohub.exception.AutoHubException;
import com.autohub.expense.entity.Invoice;
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

    private static final String UNDERSCORE = "_";
    private static final char UNDERSCORE_CHAR = '_';
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final InvoiceProcessorService invoiceProcessorService;
    private final ObjectMapper objectMapper;

    public DebeziumListener(Configuration connectorConfiguration,
                            InvoiceProcessorService invoiceProcessorService,
                            ObjectMapper objectMapper) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(connectorConfiguration.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.invoiceProcessorService = invoiceProcessorService;
        this.objectMapper = objectMapper;
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
                notifyCustomer(payload, operation);

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
                .collect(Collectors.toMap(this::getUpdatedFieldName, struct::get));
    }

    private String getUpdatedFieldName(String fieldName) {
        if (fieldName.contains(UNDERSCORE)) {
            return CaseUtils.toCamelCase(fieldName, false, UNDERSCORE_CHAR);
        }

        return fieldName;
    }

    private void notifyCustomer(Map<String, Object> payload, Operation operation) {
        Invoice invoice = objectMapper.convertValue(payload, Invoice.class);

        if (Operation.UPDATE.equals(operation) && ObjectUtils.isNotEmpty(invoice.getTotalAmount())) {
            invoiceProcessorService.processInvoice(invoice);
        }
    }

}
