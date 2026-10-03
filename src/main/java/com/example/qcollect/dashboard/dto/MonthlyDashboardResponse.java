package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyDashboardResponse {

    private String month;

    private Long submitted;

    private Long approved;

}