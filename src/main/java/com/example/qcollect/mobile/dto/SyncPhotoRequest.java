package com.example.qcollect.mobile.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncPhotoRequest {

    private String fileName;

    private String filePath;

    /**
     * Base64 image data or a data URI. Required when syncing a new offline photo.
     */
    private String image;

    private Double latitude;

    private Double longitude;

    private Boolean deleted;
}