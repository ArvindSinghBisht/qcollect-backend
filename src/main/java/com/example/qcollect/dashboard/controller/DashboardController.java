package com.example.qcollect.dashboard.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.dashboard.dto.*;
import com.example.qcollect.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    private UUID getLoggedInUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    /**
     * Global Dashboard Counts
     */
    @GetMapping("/counts")
    public ApiResponse<DashboardCountsResponse> getCounts() {

        return ApiResponse.<DashboardCountsResponse>builder()
                .success(true)
                .message("Dashboard counts fetched successfully")
                .data(dashboardService.getDashboardCounts())
                .build();
    }

    /**
     * Project Dashboard
     */
    @GetMapping("/projects/{projectId}")
    public ApiResponse<ProjectDashboardResponse> getProjectDashboard(
            @PathVariable UUID projectId,
            Authentication authentication) {

        return ApiResponse.<ProjectDashboardResponse>builder()
                .success(true)
                .message("Project dashboard fetched successfully")
                .data(
                        dashboardService.getProjectDashboard(
                                projectId,
                                getLoggedInUserId(authentication)))
                .build();
    }

    /**
     * Project Progress
     */
    @GetMapping("/projects/{projectId}/progress")
    public ApiResponse<ProgressResponse> getProjectProgress(
            @PathVariable UUID projectId,
            Authentication authentication) {

        return ApiResponse.<ProgressResponse>builder()
                .success(true)
                .message("Project progress fetched successfully")
                .data(
                        dashboardService.getProjectProgress(
                                projectId,
                                getLoggedInUserId(authentication)))
                .build();
    }

    /**
     * Monthly Dashboard
     */
    @GetMapping("/projects/{projectId}/monthly")
    public ApiResponse<List<MonthlyDashboardResponse>> getMonthlyDashboard(
            @PathVariable UUID projectId,
            Authentication authentication) {

        return ApiResponse.<List<MonthlyDashboardResponse>>builder()
                .success(true)
                .message("Monthly dashboard fetched successfully")
                .data(
                        dashboardService.getMonthlyDashboard(
                                projectId,
                                getLoggedInUserId(authentication)))
                .build();
    }

    /**
     * Submission Trends
     */
    @GetMapping("/trends")
    public ApiResponse<List<TrendResponse>> getSubmissionTrend(
            @RequestParam(defaultValue = "30") int days) {

        return ApiResponse.<List<TrendResponse>>builder()
                .success(true)
                .message("Submission trends fetched successfully")
                .data(dashboardService.getSubmissionTrend(days))
                .build();
    }

    /**
     * Recent Activities
     */
    @GetMapping("/activities")
    public ApiResponse<List<ActivityResponse>> getRecentActivities() {

        return ApiResponse.<List<ActivityResponse>>builder()
                .success(true)
                .message("Recent activities fetched successfully")
                .data(dashboardService.getRecentActivities())
                .build();
    }

    /**
     * Accessor Dashboard
     */
    @GetMapping("/accessor")
    public ApiResponse<AccessorDashboardResponse> getAccessorDashboard(
            Authentication authentication) {

        return ApiResponse.<AccessorDashboardResponse>builder()
                .success(true)
                .message("Accessor dashboard fetched successfully")
                .data(
                        dashboardService.getAccessorDashboard(
                                getLoggedInUserId(authentication)))
                .build();
    }

    /**
     * Quality Checker Dashboard
     */
    @GetMapping("/quality-checker")
    public ApiResponse<QualityCheckerDashboardResponse> getQualityCheckerDashboard(
            Authentication authentication) {

        return ApiResponse.<QualityCheckerDashboardResponse>builder()
                .success(true)
                .message("Quality Checker dashboard fetched successfully")
                .data(
                        dashboardService.getQualityCheckerDashboard(
                                getLoggedInUserId(authentication)))
                .build();
    }
}