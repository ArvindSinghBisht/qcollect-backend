package com.example.qcollect.audit.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private UUID id;

    private UUID userId;

    private String userName;

    private String module;

    private String action;

    private UUID entityId;

    private String description;

    private String ipAddress;

    private LocalDateTime createdAt;
}