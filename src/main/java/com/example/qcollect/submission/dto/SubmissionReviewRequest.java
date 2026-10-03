package com.example.qcollect.submission.dto;



import lombok.Data;

import java.util.List;

@Data
public class SubmissionReviewRequest {

    private List<FlagQuestionRequest> flaggedQuestions;

    private String remarks;
}