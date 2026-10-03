package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityCheckRequest {

    @NotNull(message = "Submission status is required.")
    private SubmissionStatus status;

    @Size(
            max = 500,
            message = "Remarks cannot exceed 500 characters."
    )
    private String remarks;
}