package com.example.qcollect.submission.repository;

import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionPhotoRepository
        extends JpaRepository<SubmissionPhoto, UUID> {

    List<SubmissionPhoto> findBySubmission_Id(UUID submissionId);
    List<SubmissionPhoto> findBySubmission_IdAndDeletedFalse(UUID submissionId);
    Optional<SubmissionPhoto> findByMobilePhotoId(UUID mobilePhotoId);
    List<SubmissionPhoto> findByUpdatedAtAfter(
            LocalDateTime updatedAt);
    List<SubmissionPhoto> findByDeletedTrue();
    List<SubmissionPhoto> findByUploadStatus(
            PhotoUploadStatus status
    );

}