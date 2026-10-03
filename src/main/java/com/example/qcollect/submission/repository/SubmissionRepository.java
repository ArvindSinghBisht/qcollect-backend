
package com.example.qcollect.submission.repository;

import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Optional<Submission> findByIdAndActiveTrue(UUID id);

    List<Submission> findByAccessor_IdAndActiveTrue(UUID accessorId);

    List<Submission> findByStatusAndActiveTrue(
            SubmissionStatus status
    );

    List<Submission> findByStatus(
            SubmissionStatus status
    );

    List<Submission> findByForm_Project_IdAndStatus(
            UUID projectId,
            SubmissionStatus status
    );

    List<Submission> findByForm_Project_IdInAndStatus(
            List<UUID> projectIds,
            SubmissionStatus status
    );

    List<Submission> findByForm_Project_Id(
            UUID projectId
    );

    List<Submission> findByForm_Id(
            UUID formId
    );

    List<Submission> findByUpdatedAtAfter(
            LocalDateTime updatedAt
    );

    List<Submission> findByDeletedTrue();

    List<Submission> findByAccessor_IdAndDeletedFalseOrderByCreatedAtDesc(UUID accessorId);

    List<Submission> findByAccessor_IdAndDeletedFalse(UUID accessorId);

    Optional<Submission> findByIdAndDeletedFalse(UUID submissionId);
    List<Submission> findByQualityChecker_IdAndStatusAndDeletedFalse(
            UUID qualityCheckerId,
            SubmissionStatus status
    );
}