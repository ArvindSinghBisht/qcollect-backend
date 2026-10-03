package com.example.qcollect.sync.mapper;

import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.sync.dto.SubmissionPhotoSyncResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionPhotoSyncMapper {

    @Mapping(target = "submissionId",
            source = "submission.id")
    SubmissionPhotoSyncResponse toResponse(
            SubmissionPhoto photo);
}