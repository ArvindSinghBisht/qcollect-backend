package com.example.qcollect.sync.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SubmissionReviewSyncResponse {

    private UUID submissionId;

    private SubmissionStatus status;

    private String remarks;

    private UUID qualityCheckerId;

    private LocalDateTime checkedAt;

    private Integer syncVersion;

    // NEW
    private List<QuestionReviewSyncResponse> questionReviews;
}