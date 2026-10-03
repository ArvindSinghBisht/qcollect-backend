package com.example.qcollect.sync.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLogResponse {

    private String deviceId;

    private Integer uploadedCount;

    private Integer downloadedCount;

    private Boolean success;

    private String message;

    private LocalDateTime createdAt;

}