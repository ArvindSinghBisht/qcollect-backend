package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessorDashboardResponse {

    private Long totalSubmissions;

    private Long draft;

    private Long submitted;

    private Long approved;

    private Long rejected;

}