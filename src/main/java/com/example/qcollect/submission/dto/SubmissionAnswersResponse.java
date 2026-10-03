package com.example.qcollect.submission.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAnswersResponse {

    private UUID submissionId;

    private String answersJson;

}
