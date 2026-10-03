package com.example.qcollect.profile.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LoginHistoryResponse {

    private LocalDateTime loginTime;

    private String ipAddress;

    private String device;

    private String browser;

    private String status;
}