package com.example.qcollect.profile.entity;

import com.example.qcollect.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginActivity extends BaseEntity {

    private UUID userId;

    private String ipAddress;

    private String device;

    private String browser;

    private String operatingSystem;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    private Boolean activeSession;
}