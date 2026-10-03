package com.example.qcollect.sync.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncConflictDto {

    private UUID submissionId;

    private Integer serverVersion;

    private Integer clientVersion;

    private String reason;
}