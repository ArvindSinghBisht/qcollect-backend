package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.ReviewStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionPhotoReviewResponse {

    private UUID id;

    private UUID photoId;

    private ReviewStatus status;

    private String remarks;

    private UUID reviewedBy;

    private LocalDateTime reviewedAt;

}