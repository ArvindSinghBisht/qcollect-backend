package com.example.qcollect.audit.service;

import com.example.qcollect.audit.dto.CreateAuditLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditHelper {

    private final AuditLogService auditLogService;

    public void log(
            UUID userId,
            String module,
            String action,
            UUID entityId,
            String description,
            String ipAddress) {

        auditLogService.createAuditLog(

                CreateAuditLogRequest.builder()
                        .userId(userId)
                        .module(module)
                        .action(action)
                        .entityId(entityId)
                        .description(description)
                        .ipAddress(ipAddress)
                        .build());
    }
}