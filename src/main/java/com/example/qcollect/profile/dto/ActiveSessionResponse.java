package com.example.qcollect.profile.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ActiveSessionResponse {

    private String device;

    private String browser;

    private String operatingSystem;

    private String ipAddress;

    private LocalDateTime loginTime;

    private LocalDateTime lastSeen;

    private Boolean currentSession;
}