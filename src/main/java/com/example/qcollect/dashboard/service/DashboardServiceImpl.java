package com.example.qcollect.dashboard.service;

import com.example.qcollect.dashboard.dto.*;
import com.example.qcollect.dashboard.repository.DashboardRepository;
import com.example.qcollect.project.enums.ProjectRole;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import com.example.qcollect.submission.enums.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    private final ProjectAuthorizationService authorizationService;

    @Override
    public ProjectDashboardResponse getProjectDashboard(
            UUID projectId,
            UUID loggedInUserId) {

        authorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId);

        Long forms =
                dashboardRepository.countForms(projectId);

        Long accessors =
                dashboardRepository.countMembersByRole(
                        projectId,
                        ProjectRole.ACCESSOR.name());

        Long qualityCheckers =
                dashboardRepository.countMembersByRole(
                        projectId,
                        ProjectRole.QUALITY_CHECKER.name());

        Long draft =
                dashboardRepository.countByStatus(
                        projectId,
                        SubmissionStatus.DRAFT);

        Long submitted =
                dashboardRepository.countByStatus(
                        projectId,
                        SubmissionStatus.SUBMITTED);

        Long approved =
                dashboardRepository.countByStatus(
                        projectId,
                        SubmissionStatus.APPROVED);

        Long rejected =
                dashboardRepository.countByStatus(
                        projectId,
                        SubmissionStatus.REJECTED);

        return ProjectDashboardResponse.builder()
                .forms(forms == null ? 0L : forms)
                .accessors(accessors == null ? 0L : accessors)
                .qualityCheckers(
                        qualityCheckers == null ? 0L : qualityCheckers)
                .draftSubmissions(draft == null ? 0L : draft)
                .submitted(submitted == null ? 0L : submitted)
                .approved(approved == null ? 0L : approved)
                .rejected(rejected == null ? 0L : rejected)
                .build();
    }

    @Override
    public AccessorDashboardResponse getAccessorDashboard(
            UUID accessorId) {

        Long total =
                dashboardRepository.countAccessorSubmissions(accessorId);

        Long draft =
                dashboardRepository.countAccessorByStatus(
                        accessorId,
                        SubmissionStatus.DRAFT);

        Long submitted =
                dashboardRepository.countAccessorByStatus(
                        accessorId,
                        SubmissionStatus.SUBMITTED);

        Long approved =
                dashboardRepository.countAccessorByStatus(
                        accessorId,
                        SubmissionStatus.APPROVED);

        Long rejected =
                dashboardRepository.countAccessorByStatus(
                        accessorId,
                        SubmissionStatus.REJECTED);

        return AccessorDashboardResponse.builder()
                .totalSubmissions(total == null ? 0L : total)
                .draft(draft == null ? 0L : draft)
                .submitted(submitted == null ? 0L : submitted)
                .approved(approved == null ? 0L : approved)
                .rejected(rejected == null ? 0L : rejected)
                .build();
    }

    @Override
    public QualityCheckerDashboardResponse getQualityCheckerDashboard(
            UUID qualityCheckerId) {

        Long reviewed =
                dashboardRepository.countQcReviewed(
                        qualityCheckerId);

        Long pending =
                dashboardRepository.pendingReviews();

        Long approved =
                dashboardRepository.countQcByStatus(
                        qualityCheckerId,
                        SubmissionStatus.APPROVED);

        Long rejected =
                dashboardRepository.countQcByStatus(
                        qualityCheckerId,
                        SubmissionStatus.REJECTED);

        return QualityCheckerDashboardResponse.builder()
                .reviewed(reviewed == null ? 0L : reviewed)
                .pendingReviews(pending == null ? 0L : pending)
                .approved(approved == null ? 0L : approved)
                .rejected(rejected == null ? 0L : rejected)
                .build();
    }

    @Override
    public DashboardCountsResponse getDashboardCounts() {

        return DashboardCountsResponse.builder()
                .projects(
                        dashboardRepository.totalProjects())
                .forms(
                        dashboardRepository.totalForms())
                .users(
                        dashboardRepository.totalUsers())
                .submissions(
                        dashboardRepository.totalSubmissions())
                .build();
    }
    @Override
    public ProgressResponse getProjectProgress(
            UUID projectId,
            UUID loggedInUserId) {

        authorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId);

        Long approved =
                dashboardRepository.countByStatus(
                        projectId,
                        SubmissionStatus.APPROVED);

        Long total =
                dashboardRepository.countSubmissions(projectId);

        approved = approved == null ? 0L : approved;
        total = total == null ? 0L : total;

        Long remaining = Math.max(0L, total - approved);

        return ProgressResponse.builder()
                .completed(approved)
                .remaining(remaining)
                .build();
    }

    @Override
    public List<ActivityResponse> getRecentActivities() {

        return dashboardRepository
                .recentActivities()
                .stream()
                .limit(20)
                .map(submission -> ActivityResponse.builder()
                        .type("Submission")
                        .userName(
                                submission.getAccessor().getFirstName()
                                        + " "
                                        + submission.getAccessor().getLastName())
                        .description(
                                "Submitted form : "
                                        + submission.getForm().getName())
                        .createdAt(submission.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public List<TrendResponse> getSubmissionTrend(
            int lastDays) {

        LocalDateTime start =
                LocalDateTime.now().minusDays(lastDays);

        Map<LocalDate, Long> grouped =
                dashboardRepository
                        .submissionTrend(start)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getCreatedAt().toLocalDate(),
                                Collectors.counting()));

        return grouped.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        TrendResponse.builder()
                                .date(entry.getKey())
                                .total(entry.getValue())
                                .build())
                .toList();
    }

    @Override
    public List<MonthlyDashboardResponse> getMonthlyDashboard(
            UUID projectId,
            UUID loggedInUserId) {

        authorizationService.requireProjectAdmin(
                projectId,
                loggedInUserId);

        Map<Month, Long> submitted =
                dashboardRepository
                        .findByForm_Project_IdAndStatus(
                                projectId,
                                SubmissionStatus.SUBMITTED)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getCreatedAt().getMonth(),
                                Collectors.counting()));

        Map<Month, Long> approved =
                dashboardRepository
                        .findByForm_Project_IdAndStatus(
                                projectId,
                                SubmissionStatus.APPROVED)
                        .stream()
                        .collect(Collectors.groupingBy(
                                s -> s.getCreatedAt().getMonth(),
                                Collectors.counting()));

        List<MonthlyDashboardResponse> result =
                new ArrayList<>();

        for (Month month : Month.values()) {

            result.add(
                    MonthlyDashboardResponse.builder()
                            .month(month.name())
                            .submitted(
                                    submitted.getOrDefault(month, 0L))
                            .approved(
                                    approved.getOrDefault(month, 0L))
                            .build());
        }

        return result;
    }
    @Override
    public void refreshDashboardCache() {

        // Future implementation:
        // - Reload dashboard counts
        // - Warm up cache
        // - Refresh statistics
    }
}