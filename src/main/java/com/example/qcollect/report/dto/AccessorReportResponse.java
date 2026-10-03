package com.example.qcollect.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessorReportResponse {

    private String accessorName;

    private Long totalSubmitted;

    private Long approved;

    private Long rejected;

    private Long pending;

}