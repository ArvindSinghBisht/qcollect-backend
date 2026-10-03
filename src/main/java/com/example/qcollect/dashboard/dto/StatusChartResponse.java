package com.example.qcollect.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusChartResponse {

    private long approved;

    private long rejected;

    private long submitted;

    private long draft;
}