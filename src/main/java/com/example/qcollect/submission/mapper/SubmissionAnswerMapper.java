package com.example.qcollect.submission.mapper;

import com.example.qcollect.submission.dto.SubmissionAnswerResponse;
import com.example.qcollect.submission.entity.SubmissionAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionAnswerMapper {

    @Mapping(target = "fieldId", source = "field.id")
    @Mapping(target = "fieldName", source = "field.label")
    @Mapping(target = "fieldType", source = "field.fieldType")
    SubmissionAnswerResponse toResponse(
            SubmissionAnswer answer
    );

}