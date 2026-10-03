package com.example.qcollect.dashboard.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalForms;

    private long totalSubmissions;

    private long approved;

    private long rejected;

    private long pending;

    private long draft;
}