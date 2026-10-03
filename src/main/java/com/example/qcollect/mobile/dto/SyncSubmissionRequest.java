package com.example.qcollect.mobile.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncSubmissionRequest {

    @NotNull
    private UUID submissionId;

    @NotNull
    private Integer syncVersion;

    private String answersJson;

    private List<SyncPhotoRequest> photos;
    private Boolean deleted;
}