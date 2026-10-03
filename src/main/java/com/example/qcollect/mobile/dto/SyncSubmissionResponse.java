package com.example.qcollect.mobile.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncSubmissionResponse {

    private UUID submissionId;

    private Boolean synced;

    private Integer syncVersion;
}