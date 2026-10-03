package com.example.qcollect.submission.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionPhotoResponse {

    private UUID id;

    private String fileName;

    private String fileUrl;

    private Double latitude;

    private Double longitude;
}