package com.example.qcollect.sync.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionPhotoSyncResponse {

    private UUID id;

    private UUID submissionId;

    private String photoUrl;

    private Double latitude;

    private Double longitude;

    private Integer syncVersion;

    private Boolean synced;

    private LocalDateTime syncedAt;

    private String lastModifiedDevice;
}