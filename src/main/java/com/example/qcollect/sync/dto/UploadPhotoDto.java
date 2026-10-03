package com.example.qcollect.sync.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadPhotoDto {

    private UUID photoId;

    private UUID submissionId;

    /**
     * Base64 encoded image
     */
    private String image;

    private Double latitude;

    private Double longitude;

    private Long capturedAt;

    private Integer syncVersion;
}