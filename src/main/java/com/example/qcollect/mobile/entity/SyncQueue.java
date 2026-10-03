package com.example.qcollect.mobile.entity;

import com.example.qcollect.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sync_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncQueue extends BaseEntity {

    @Column(nullable = false)
    private UUID submissionId;

    @Column(nullable = false)
    private UUID userId;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status;

    @Builder.Default
    private Integer retryCount = 0;

    private LocalDateTime lastAttempt;

    @Column(length = 1000)
    private String errorMessage;
}