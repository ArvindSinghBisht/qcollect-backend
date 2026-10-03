package com.example.qcollect.report.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionReportResponse {

    private UUID submissionId;

    private String projectName;

    private String formName;

    private String accessorName;

    private String qualityCheckerName;

    private SubmissionStatus status;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;
}