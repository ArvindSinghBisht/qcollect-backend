package com.example.qcollect.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySubmissionResponse {

    private String month;

    private Long submissions;
}