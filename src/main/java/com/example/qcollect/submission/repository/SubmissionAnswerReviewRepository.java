package com.example.qcollect.submission.repository;

import com.example.qcollect.submission.entity.SubmissionAnswerReview;
import com.example.qcollect.submission.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionAnswerReviewRepository
        extends JpaRepository<SubmissionAnswerReview, UUID> {

    Optional<SubmissionAnswerReview> findBySubmissionAnswer_Id(
            UUID answerId
    );

    List<SubmissionAnswerReview> findBySubmissionAnswer_Submission_Id(
            UUID submissionId
    );
    boolean existsBySubmissionAnswer_Submission_IdAndStatus(
            UUID submissionId,
            ReviewStatus status
    );
    List<SubmissionAnswerReview> findBySubmissionAnswer_Submission_IdAndStatus(
            UUID submissionId,
            ReviewStatus status
    );
}