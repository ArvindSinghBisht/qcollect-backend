package com.example.qcollect.dashboard.repository;

import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DashboardRepository
        extends JpaRepository<Submission, UUID> {

    // ==========================
    // PROJECT DASHBOARD
    // ==========================

    @Query("""
        SELECT COUNT(f)
        FROM Form f
        WHERE f.project.id = :projectId
          AND f.active = true
    """)
    Long countForms(UUID projectId);

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.form.project.id = :projectId
    """)
    Long countSubmissions(UUID projectId);

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.form.project.id = :projectId
          AND s.status = :status
    """)
    Long countByStatus(
            UUID projectId,
            SubmissionStatus status
    );

    @Query("""
        SELECT COUNT(pu)
        FROM ProjectUser pu
        JOIN Role r ON pu.roleId = r.id
        WHERE pu.projectId = :projectId
          AND r.name = :role
    """)
    Long countMembersByRole(
            UUID projectId,
            String role
    );

    // ==========================
    // ACCESSOR DASHBOARD
    // ==========================

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.accessor.id = :userId
    """)
    Long countAccessorSubmissions(UUID userId);

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.accessor.id = :userId
          AND s.status = :status
    """)
    Long countAccessorByStatus(
            UUID userId,
            SubmissionStatus status
    );

    // ==========================
    // QUALITY CHECKER
    // ==========================

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.qualityChecker.id = :userId
    """)
    Long countQcReviewed(UUID userId);

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.status='SUBMITTED'
    """)
    Long pendingReviews();

    // ==========================
    // GLOBAL COUNTS
    // ==========================

    @Query("""
        SELECT COUNT(p)
        FROM Project p
    """)
    Long totalProjects();

    @Query("""
        SELECT COUNT(f)
        FROM Form f
    """)
    Long totalForms();

    @Query("""
        SELECT COUNT(u)
        FROM User u
    """)
    Long totalUsers();

    @Query("""
        SELECT COUNT(s)
        FROM Submission s
    """)
    Long totalSubmissions();

    // ==========================
    // RECENT ACTIVITY
    // ==========================

    @Query("""
        SELECT s
        FROM Submission s
        ORDER BY s.createdAt DESC
    """)
    List<Submission> recentActivities();

    // ==========================
    // TREND
    // ==========================

    @Query("""
        SELECT s
        FROM Submission s
        WHERE s.createdAt >= :startDate
        ORDER BY s.createdAt
    """)
    List<Submission> submissionTrend(
            LocalDateTime startDate
    );
    @Query("""
SELECT COUNT(s)
FROM Submission s
WHERE s.status = :status
""")
    Long countAllByStatus(SubmissionStatus status);

    @Query("""
SELECT COUNT(s)
FROM Submission s
WHERE s.qualityChecker.id = :userId
AND s.status = :status
""")
    Long countQcByStatus(
            UUID userId,
            SubmissionStatus status
    );
    List<Submission> findByForm_Project_IdAndStatus(
            UUID projectId,
            SubmissionStatus status);


}