package com.example.qcollect.submission.mapper;

import com.example.qcollect.submission.dto.SubmissionPhotoReviewResponse;
import com.example.qcollect.submission.entity.SubmissionPhotoReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionPhotoReviewMapper {

    @Mapping(target = "photoId",
            source = "submissionPhoto.id")

    @Mapping(target = "reviewedBy",
            source = "reviewedBy.id")

    SubmissionPhotoReviewResponse toResponse(
            SubmissionPhotoReview review
    );

}