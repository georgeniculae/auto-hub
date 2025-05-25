package com.autohub.audit.mapper;

import com.autohub.audit.entity.BookingAuditLogInfo;
import com.autohub.audit.entity.CustomerAuditLogInfo;
import com.autohub.audit.entity.ExpenseAuditLogInfo;
import com.autohub.dto.common.AuditLogInfoRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AuditLogInfoMapper {

    BookingAuditLogInfo mapDtoToBookingEntity(AuditLogInfoRequest auditLogInfoRequest);

    CustomerAuditLogInfo mapDtoToCustomerEntity(AuditLogInfoRequest auditLogInfoRequest);

    ExpenseAuditLogInfo mapDtoToExpenseEntity(AuditLogInfoRequest auditLogInfoRequest);

}
