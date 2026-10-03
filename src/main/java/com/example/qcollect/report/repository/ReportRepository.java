package com.example.qcollect.report.repository;

import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends
        JpaRepository<Submission, UUID>,
        JpaSpecificationExecutor<Submission> {

    List<Submission> findByForm_Project_Id(
            UUID projectId);

    List<Submission> findByForm_Id(
            UUID formId);

    List<Submission> findByAccessor_IdAndDeletedFalse(
            UUID accessorId);

    List<Submission> findByQualityChecker_Id(
            UUID qualityCheckerId);

    List<Submission> findByStatus(
            SubmissionStatus status);

    List<Submission> findBySubmittedAtBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<Submission> findByForm_Project_IdAndStatus(
            UUID projectId,
            SubmissionStatus status);

    List<Submission> findByForm_Project_IdAndSubmittedAtBetween(
            UUID projectId,
            LocalDateTime from,
            LocalDateTime to);

    List<Submission> findByForm_Project_IdAndAccessor_Id(
            UUID projectId,
            UUID accessorId);

    List<Submission> findByForm_Project_IdAndQualityChecker_Id(
            UUID projectId,
            UUID qualityCheckerId);


}