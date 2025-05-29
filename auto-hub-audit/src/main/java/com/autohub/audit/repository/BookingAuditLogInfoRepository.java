package com.autohub.audit.repository;

import com.autohub.audit.entity.AuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingAuditLogInfoRepository extends JpaRepository<AuditLogInfo, Long> {
}
