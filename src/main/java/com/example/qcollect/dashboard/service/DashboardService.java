package com.example.qcollect.dashboard.service;

import com.example.qcollect.dashboard.dto.*;

import java.util.List;
import java.util.UUID;

public interface DashboardService {

    ProjectDashboardResponse getProjectDashboard(
            UUID projectId,
            UUID loggedInUserId);

    AccessorDashboardResponse getAccessorDashboard(
            UUID accessorId);

    QualityCheckerDashboardResponse getQualityCheckerDashboard(
            UUID qualityCheckerId);

    DashboardCountsResponse getDashboardCounts();

    ProgressResponse getProjectProgress(
            UUID projectId,
            UUID loggedInUserId);

    List<ActivityResponse> getRecentActivities();

    List<TrendResponse> getSubmissionTrend(
            int lastDays);

    List<MonthlyDashboardResponse> getMonthlyDashboard(
            UUID projectId,
            UUID loggedInUserId);

    void refreshDashboardCache();
}