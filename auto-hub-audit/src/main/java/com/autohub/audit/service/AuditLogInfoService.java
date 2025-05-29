package com.autohub.audit.service;

import com.autohub.audit.entity.AuditLogInfo;
import com.autohub.audit.mapper.AuditLogInfoMapper;
import com.autohub.audit.repository.BookingAuditLogInfoRepository;
import com.autohub.dto.common.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogInfoService {

    private final AuditLogInfoMapper auditLogInfoMapper;
    private final BookingAuditLogInfoRepository bookingAuditLogInfoRepository;

    public void saveAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        AuditLogInfo auditLogInfo = auditLogInfoMapper.mapDtoToBookingEntity(auditLogInfoRequest);
        bookingAuditLogInfoRepository.save(auditLogInfo);
    }

}
