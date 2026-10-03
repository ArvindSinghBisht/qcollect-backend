package com.example.qcollect.audit.mapper;

import com.example.qcollect.audit.dto.AuditLogResponse;
import com.example.qcollect.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(
            AuditLog auditLog) {

        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(
                        auditLog.getUser() == null
                                ? null
                                : auditLog.getUser().getId())
                .userName(
                        auditLog.getUser() == null
                                ? null
                                : auditLog.getUser().getFirstName()
                                + " "
                                + auditLog.getUser().getLastName())
                .module(auditLog.getModule())
                .action(auditLog.getAction())
                .entityId(auditLog.getEntityId())
                .description(auditLog.getDescription())
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}