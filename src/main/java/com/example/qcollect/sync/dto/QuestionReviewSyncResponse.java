package com.example.qcollect.sync.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuestionReviewSyncResponse {

    private UUID answerId;

    private UUID questionId;

    private boolean flagged;

    private String qcComment;

    private UUID qualityCheckerId;

    private LocalDateTime reviewedAt;
}