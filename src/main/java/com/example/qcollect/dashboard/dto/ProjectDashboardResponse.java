package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDashboardResponse {

    private Long forms;

    private Long accessors;

    private Long qualityCheckers;

    private Long draftSubmissions;

    private Long submitted;

    private Long approved;

    private Long rejected;

}