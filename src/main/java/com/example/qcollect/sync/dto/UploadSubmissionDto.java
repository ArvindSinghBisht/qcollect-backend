package com.example.qcollect.sync.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSubmissionDto {

    private UUID submissionId;

    private UUID formId;

    private Double latitude;

    private Double longitude;
    private Integer syncVersion;
}