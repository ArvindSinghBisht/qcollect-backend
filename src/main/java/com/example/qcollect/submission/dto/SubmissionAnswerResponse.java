package com.example.qcollect.submission.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAnswerResponse {

    private UUID id;

    private UUID fieldId;

    private String fieldName;

    private String fieldType;

    private String value;

}