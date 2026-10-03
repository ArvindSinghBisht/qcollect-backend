package com.example.qcollect.sync.mapper;

import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.sync.dto.SubmissionReviewSyncResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionReviewSyncMapper {

    @Mapping(target = "submissionId", source = "id")
    @Mapping(target = "qualityCheckerId", source = "qualityChecker.id")
    SubmissionReviewSyncResponse toResponse(
            Submission submission
    );

}