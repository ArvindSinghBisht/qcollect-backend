package com.example.qcollect.submission.repository;

import com.example.qcollect.submission.entity.SubmissionPhotoReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionPhotoReviewRepository
        extends JpaRepository<SubmissionPhotoReview, UUID> {

    Optional<SubmissionPhotoReview> findBySubmissionPhoto_Id(
            UUID photoId
    );

}