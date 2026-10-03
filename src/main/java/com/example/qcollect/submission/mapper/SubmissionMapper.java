package com.example.qcollect.submission.mapper;

import com.example.qcollect.submission.dto.SubmissionResponse;
import com.example.qcollect.submission.entity.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public SubmissionResponse toResponse(Submission submission){

        return SubmissionResponse.builder()

                .id(submission.getId())

                .formId(submission.getForm().getId())

                .accessorId(submission.getAccessor().getId())

                .status(submission.getStatus())

                .latitude(submission.getLatitude())

                .longitude(submission.getLongitude())

                .submittedAt(submission.getSubmittedAt())

                .build();
    }

}