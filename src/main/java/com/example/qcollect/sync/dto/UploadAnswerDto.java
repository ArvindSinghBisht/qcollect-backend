package com.example.qcollect.sync.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadAnswerDto {

    private UUID answerId;

    private UUID submissionId;

    private UUID fieldId;

    private String value;

    private Integer syncVersion;
}