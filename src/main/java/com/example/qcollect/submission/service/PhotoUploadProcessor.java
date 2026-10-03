package com.example.qcollect.submission.service;

import com.example.qcollect.integration.cloudinary.CloudinaryStorageService;
import com.example.qcollect.integration.cloudinary.CloudinaryUploadResult;
import com.example.qcollect.storage.PhotoStorageService;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import com.example.qcollect.submission.repository.SubmissionPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoUploadProcessor {

    private final SubmissionPhotoRepository photoRepository;
    private final PhotoStorageService localPhotoStorageService;
    private final CloudinaryStorageService cloudinaryStorageService;

    /**
     * Migrates legacy locally stored pending photos to Cloudinary.
     * New multipart and base64 uploads go directly to Cloudinary and are
     * therefore saved with UPLOADED status immediately.
     */
    @Scheduled(fixedDelayString = "${cloudinary.legacy-migration-delay-ms:30000}")
    public void uploadLegacyPhotos() {
        List<SubmissionPhoto> pending =
                photoRepository.findByUploadStatus(PhotoUploadStatus.PENDING);

        for (SubmissionPhoto photo : pending) {
            String legacyPath = photo.getFilePath();

            try {
                photo.setUploadStatus(PhotoUploadStatus.UPLOADING);
                photoRepository.save(photo);

                Resource resource = localPhotoStorageService.load(legacyPath);

                byte[] bytes;
                try (var inputStream = resource.getInputStream()) {
                    bytes = inputStream.readAllBytes();
                }

                CloudinaryUploadResult uploaded =
                        cloudinaryStorageService.uploadBytes(
                                bytes,
                                photo.getFileName(),
                                photo.getSubmission().getForm().getProject().getId(),
                                photo.getSubmission().getId()
                        );

                photo.setFilePath(uploaded.publicId());
                photo.setPhotoUrl(uploaded.secureUrl());
                photo.setFileName(uploaded.originalFileName());
                photo.setUploadStatus(PhotoUploadStatus.UPLOADED);
                photo.setSynced(true);
                photo.setSyncedAt(LocalDateTime.now());
                photoRepository.save(photo);

                try {
                    localPhotoStorageService.delete(legacyPath);
                } catch (Exception cleanupError) {
                    log.warn(
                            "Cloudinary upload succeeded but legacy local photo could not be deleted: {}",
                            legacyPath,
                            cleanupError
                    );
                }

            } catch (Exception ex) {
                photo.setUploadStatus(PhotoUploadStatus.FAILED);
                photoRepository.save(photo);

                log.error("Cloudinary migration failed for photo {}", photo.getId(), ex);
            }
        }
    }
}
