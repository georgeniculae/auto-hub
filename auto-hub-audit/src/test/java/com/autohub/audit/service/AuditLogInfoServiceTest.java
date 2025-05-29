package com.autohub.audit.service;

import com.autohub.audit.entity.AuditLogInfo;
import com.autohub.audit.mapper.AuditLogInfoMapper;
import com.autohub.audit.mapper.AuditLogInfoMapperImpl;
import com.autohub.audit.repository.BookingAuditLogInfoRepository;
import com.autohub.audit.util.TestUtil;
import com.autohub.dto.common.AuditLogInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogInfoServiceTest {

    @InjectMocks
    private AuditLogInfoService auditLogInfoService;

    @Mock
    private BookingAuditLogInfoRepository bookingAuditLogInfoRepository;

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void saveAuditLogInfoTest_success() {
        AuditLogInfo auditLogInfo =
                TestUtil.getResourceAsJson("/data/AuditLogInfo.json", AuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(bookingAuditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(auditLogInfo);

        assertDoesNotThrow(() -> auditLogInfoService.saveAuditLogInfo(auditLogInfoRequest));

        verify(auditLogInfoMapper).mapDtoToBookingEntity(any(AuditLogInfoRequest.class));
    }

}
