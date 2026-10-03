package com.example.qcollect.sync.mapper;

import com.example.qcollect.submission.entity.SubmissionAnswer;
import com.example.qcollect.sync.dto.SubmissionAnswerSyncResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionAnswerSyncMapper {

    @Mapping(target = "answerId", source = "id")
    @Mapping(target = "submissionId", source = "submission.id")
    @Mapping(target = "fieldId", source = "field.id")
    SubmissionAnswerSyncResponse toResponse(
            SubmissionAnswer answer);
}