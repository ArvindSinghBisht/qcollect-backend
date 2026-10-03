package com.example.qcollect.submission.controller;

import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.storage.PhotoStorageService;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.submission.repository.SubmissionPhotoRepository;
import com.example.qcollect.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photos")
public class PhotoController {

    private final SubmissionPhotoRepository photoRepository;
    private final SubmissionService submissionService;
    private final PhotoStorageService photoStorageService;

    @GetMapping("/{photoId}")
    public ResponseEntity<?> downloadPhoto(

            @PathVariable UUID photoId

    ) throws Exception {

        SubmissionPhoto photo =
                photoRepository.findById(photoId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Photo not found"));

        if (photo.getPhotoUrl() != null && !photo.getPhotoUrl().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(photo.getPhotoUrl()))
                    .build();
        }

        Resource resource =
                photoStorageService.load(
                        photo.getFilePath());

        return ResponseEntity.ok()

                .contentType(MediaType.IMAGE_JPEG)

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getFileName() + "\""
                )

                .body(resource);
    }
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(

            @PathVariable UUID photoId,

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        submissionService.deletePhoto(
                photoId,
                loggedInUserId
        );

        return ResponseEntity.noContent().build();
    }
}