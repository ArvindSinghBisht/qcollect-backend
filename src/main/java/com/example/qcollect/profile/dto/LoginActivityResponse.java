package com.example.qcollect.profile.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LoginActivityResponse {

    private UUID id;

    private String browser;

    private String device;

    private String operatingSystem;

    private String ipAddress;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    private Boolean activeSession;

}