package com.example.qcollect.submission.repository;

import com.example.qcollect.submission.entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionAnswerRepository
        extends JpaRepository<SubmissionAnswer, UUID> {

    List<SubmissionAnswer> findBySubmission_Id(UUID submissionId);

    Optional<SubmissionAnswer> findBySubmission_IdAndField_Id(
            UUID submissionId,
            UUID fieldId
    );
    Optional<SubmissionAnswer> findById(UUID id);
    List<SubmissionAnswer> findByUpdatedAtAfter(
            LocalDateTime updatedAt);
    List<SubmissionAnswer> findByDeletedTrue();
    List<SubmissionAnswer> findBySubmission_IdOrderById(UUID submissionId);

}