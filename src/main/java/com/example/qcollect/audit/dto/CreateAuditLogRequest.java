package com.example.qcollect.audit.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditLogRequest {

    private UUID userId;

    private String module;

    private String action;

    private UUID entityId;

    private String description;

    private String ipAddress;
}