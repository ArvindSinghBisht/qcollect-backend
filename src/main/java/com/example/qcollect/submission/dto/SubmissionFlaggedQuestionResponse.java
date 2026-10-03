package com.example.qcollect.submission.dto;

//package com.example.qcollect.submission.dto;

import com.example.qcollect.submission.enums.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SubmissionFlaggedQuestionResponse {

    private UUID answerId;

    private UUID fieldId;

    private ReviewStatus status;

    private String comment;
}