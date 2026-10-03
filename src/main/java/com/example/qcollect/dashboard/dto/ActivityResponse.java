package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityResponse {

    private String type;

    private String userName;

    private String description;

    private LocalDateTime createdAt;
}