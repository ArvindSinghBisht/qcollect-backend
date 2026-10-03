package com.example.qcollect.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectReportResponse {

    private Long totalForms;

    private Long totalSubmissions;

    private Long draft;

    private Long submitted;

    private Long approved;

    private Long rejected;

}