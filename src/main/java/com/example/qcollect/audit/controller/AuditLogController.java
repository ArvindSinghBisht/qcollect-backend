package com.example.qcollect.audit.controller;

import com.example.qcollect.audit.dto.AuditLogResponse;
import com.example.qcollect.audit.service.AuditLogService;
import com.example.qcollect.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<List<AuditLogResponse>> getAllLogs() {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("Audit logs fetched successfully")
                .data(auditLogService.getAllLogs())
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AuditLogResponse>> getUserLogs(
            @PathVariable UUID userId) {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("User audit logs fetched successfully")
                .data(auditLogService.getUserLogs(userId))
                .build();
    }

    @GetMapping("/module/{module}")
    public ApiResponse<List<AuditLogResponse>> getModuleLogs(
            @PathVariable String module) {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("Module audit logs fetched successfully")
                .data(auditLogService.getModuleLogs(module))
                .build();
    }

    @GetMapping("/user/{userId}/module/{module}")
    public ApiResponse<List<AuditLogResponse>> getUserModuleLogs(
            @PathVariable UUID userId,
            @PathVariable String module) {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("User module audit logs fetched successfully")
                .data(auditLogService.getUserModuleLogs(
                        userId,
                        module))
                .build();
    }

    @GetMapping("/between")
    public ApiResponse<List<AuditLogResponse>> getLogsBetween(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("Audit logs fetched successfully")
                .data(auditLogService.getLogsBetween(from, to))
                .build();
    }
}