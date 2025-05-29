package com.autohub.audit.consumer;

import com.autohub.audit.service.AuditLogInfoService;
import com.autohub.audit.util.TestUtil;
import com.autohub.dto.common.AuditLogInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class AuditLogInfoMessageConsumerTest {

    @InjectMocks
    private AuditLogInfoMessageConsumer auditLogInfoMessageConsumer;

    @Mock
    private AuditLogInfoService auditLogInfoService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void bookingAuditInfoConsumerTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = MessageBuilder.withPayload(auditLogInfoRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(auditLogInfoService).saveAuditLogInfo(any(AuditLogInfoRequest.class));

        auditLogInfoMessageConsumer.auditInfoConsumer().accept(message);
    }

}
