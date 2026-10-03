package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QualityCheckerDashboardResponse {

    private Long reviewed;

    private Long pendingReviews;

    private Long approved;

    private Long rejected;

}