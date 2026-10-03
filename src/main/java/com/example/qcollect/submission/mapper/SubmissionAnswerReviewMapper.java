package com.example.qcollect.submission.mapper;

import com.example.qcollect.submission.dto.SubmissionAnswerReviewResponse;
import com.example.qcollect.submission.entity.SubmissionAnswerReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionAnswerReviewMapper {

    @Mapping(target = "answerId",
            source = "submissionAnswer.id")

    @Mapping(target = "reviewedBy",
            source = "reviewedBy.id")

    SubmissionAnswerReviewResponse toResponse(
            SubmissionAnswerReview review
    );

}