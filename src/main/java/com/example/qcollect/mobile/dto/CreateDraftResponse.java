package com.example.qcollect.mobile.dto;

import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDraftResponse {

    private UUID submissionId;

    private UUID formId;

    private Integer formVersion;

    private SubmissionStatus status;

}