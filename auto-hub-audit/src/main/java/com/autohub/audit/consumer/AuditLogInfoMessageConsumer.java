package com.autohub.audit.consumer;

import com.autohub.audit.service.AuditLogInfoService;
import com.autohub.dto.common.AuditLogInfoRequest;
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
public class AuditLogInfoMessageConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @Bean
    public Consumer<Message<AuditLogInfoRequest>> auditInfoConsumer() {
        return this::processAudit;
    }

    private void processAudit(Message<AuditLogInfoRequest> message) {
        AuditLogInfoRequest payload = message.getPayload();
        auditLogInfoService.saveAuditLogInfo(payload);
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Audit created for {}", payload.activityDescription().toLowerCase());
    }

}
