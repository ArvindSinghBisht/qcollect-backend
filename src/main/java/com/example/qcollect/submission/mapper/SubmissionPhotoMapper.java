package com.example.qcollect.submission.mapper;

import com.example.qcollect.submission.dto.SubmissionPhotoResponse;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import org.springframework.stereotype.Component;

@Component
public class SubmissionPhotoMapper {

    public SubmissionPhotoResponse toResponse(
            SubmissionPhoto photo
    ) {

        String fileUrl;

        if (photo.getPhotoUrl() != null
                && !photo.getPhotoUrl().isBlank()) {

            // New Cloudinary photos: return direct HTTPS URL.
            fileUrl = photo.getPhotoUrl();

        } else {

            // Old locally stored photos: use backend endpoint.
            fileUrl = "/photos/" + photo.getId();
        }

        return SubmissionPhotoResponse.builder()
                .id(photo.getId())
                .fileName(photo.getFileName())
                .fileUrl(fileUrl)
                .latitude(photo.getLatitude())
                .longitude(photo.getLongitude())
                .build();
    }
}
