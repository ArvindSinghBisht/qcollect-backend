package com.example.qcollect.report.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterRequest {

    private UUID projectId;

    private UUID formId;

    private UUID accessorId;

    private UUID qualityCheckerId;

    private SubmissionStatus status;

    private LocalDate fromDate;

    private LocalDate toDate;
}