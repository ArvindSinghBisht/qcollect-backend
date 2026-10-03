package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardCountsResponse {

    private Long projects;

    private Long forms;

    private Long users;

    private Long submissions;
}