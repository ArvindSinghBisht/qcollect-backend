package com.example.qcollect.sync.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAnswerSyncResponse {

    private UUID answerId;

    private UUID submissionId;

    private UUID fieldId;

    private String value;

    private Integer syncVersion;

    private LocalDateTime updatedAt;
}