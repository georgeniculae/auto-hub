package com.autohub.audit.util;

import com.autohub.audit.entity.AuditLogInfo;
import com.autohub.dto.common.AuditLogInfoRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AssertionUtil {

    public static void assertAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest, AuditLogInfo auditLogInfo) {
        assertEquals(auditLogInfoRequest.username(), auditLogInfo.getUsername());
        assertEquals(auditLogInfoRequest.methodName(), auditLogInfo.getMethodName());
        assertFalse(auditLogInfo.getParametersInfo().isBlank());
    }

}
