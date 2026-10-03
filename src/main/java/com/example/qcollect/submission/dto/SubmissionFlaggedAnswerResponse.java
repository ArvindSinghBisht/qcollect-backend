package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SubmissionFlaggedAnswerResponse {

    private UUID answerId;

    private UUID formFieldId;

    private ReviewStatus status;

    private String remarks;

    private UUID reviewedBy;

    private LocalDateTime reviewedAt;
}