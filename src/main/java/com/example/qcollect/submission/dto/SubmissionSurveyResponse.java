package com.example.qcollect.submission.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionSurveyResponse {

    private UUID submissionId;

    private UUID formId;

    private String surveyJson;

    private String answersJson;

    private List<SubmissionFlaggedQuestionResponse> flaggedQuestions;
}