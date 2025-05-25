package com.autohub.audit.service;

import com.autohub.audit.entity.BookingAuditLogInfo;
import com.autohub.audit.entity.CustomerAuditLogInfo;
import com.autohub.audit.entity.ExpenseAuditLogInfo;
import com.autohub.audit.mapper.AuditLogInfoMapper;
import com.autohub.audit.repository.BookingAuditLogInfoRepository;
import com.autohub.audit.repository.CustomerAuditLogInfoRepository;
import com.autohub.audit.repository.ExpenseAuditLogInfoRepository;
import com.autohub.dto.common.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogInfoService {

    private final AuditLogInfoMapper auditLogInfoMapper;
    private final BookingAuditLogInfoRepository bookingAuditLogInfoRepository;
    private final CustomerAuditLogInfoRepository customerAuditLogInfoRepository;
    private final ExpenseAuditLogInfoRepository expenseAuditLogInfoRepository;

    public void saveBookingAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        BookingAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToBookingEntity(auditLogInfoRequest);
        bookingAuditLogInfoRepository.save(bookingAuditLogInfo);
    }

    public void saveCustomerAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        CustomerAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToCustomerEntity(auditLogInfoRequest);
        customerAuditLogInfoRepository.save(bookingAuditLogInfo);
    }

    public void saveExpenseAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        ExpenseAuditLogInfo expenseAuditLogInfo = auditLogInfoMapper.mapDtoToExpenseEntity(auditLogInfoRequest);
        expenseAuditLogInfoRepository.save(expenseAuditLogInfo);
    }

}
