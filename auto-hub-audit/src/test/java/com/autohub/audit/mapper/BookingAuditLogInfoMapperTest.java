package com.autohub.audit.mapper;

import com.autohub.audit.entity.BookingAuditLogInfo;
import com.autohub.audit.util.AssertionUtil;
import com.autohub.audit.util.TestUtil;
import com.autohub.dto.common.AuditLogInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingAuditLogInfoMapperTest {

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void mapDtoToEntityTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        BookingAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToBookingEntity(auditLogInfoRequest);

        AssertionUtil.assertAuditLogInfo(auditLogInfoRequest, bookingAuditLogInfo);
    }

}
