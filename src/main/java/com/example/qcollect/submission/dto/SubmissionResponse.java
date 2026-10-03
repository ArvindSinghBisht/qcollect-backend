package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SubmissionResponse {

    private UUID id;

    private UUID formId;

    private UUID accessorId;

    private SubmissionStatus status;

    private Double latitude;

    private Double longitude;

    private LocalDateTime submittedAt;

}