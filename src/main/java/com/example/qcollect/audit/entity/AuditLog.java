package com.example.qcollect.audit.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(length = 1000)
    private String description;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;
}