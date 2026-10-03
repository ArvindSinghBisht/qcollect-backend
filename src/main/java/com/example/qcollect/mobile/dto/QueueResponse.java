package com.example.qcollect.mobile.dto;

import com.example.qcollect.mobile.entity.SyncStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueResponse {

    private UUID id;

    private UUID submissionId;

    private SyncStatus status;

    private Integer retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime lastAttempt;

    private String errorMessage;
}