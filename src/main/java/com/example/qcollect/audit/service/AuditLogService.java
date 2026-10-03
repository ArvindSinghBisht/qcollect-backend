package com.example.qcollect.audit.service;

import com.example.qcollect.audit.dto.AuditLogResponse;
import com.example.qcollect.audit.dto.CreateAuditLogRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    AuditLogResponse createAuditLog(
            CreateAuditLogRequest request);

    List<AuditLogResponse> getAllLogs();

    List<AuditLogResponse> getUserLogs(
            UUID userId);

    List<AuditLogResponse> getModuleLogs(
            String module);

    List<AuditLogResponse> getLogsBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<AuditLogResponse> getUserModuleLogs(
            UUID userId,
            String module);
}