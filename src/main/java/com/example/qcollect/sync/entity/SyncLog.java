package com.example.qcollect.sync.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sync_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private Integer uploadedCount;

    @Column(nullable = false)
    private Integer downloadedCount;

    @Column(nullable = false)
    private Boolean success;

    @Column(length = 1000)
    private String message;

    @Column
    private Integer conflictCount;

    @Column(length = 50)
    private String conflictStrategy;

    @Column(columnDefinition = "TEXT")
    private String conflictSummary;
}