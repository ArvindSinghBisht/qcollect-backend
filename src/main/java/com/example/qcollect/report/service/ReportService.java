package com.example.qcollect.report.service;

import com.example.qcollect.report.dto.ReportFilterRequest;
import com.example.qcollect.report.dto.SubmissionReportResponse;
import org.springframework.data.domain.Page;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

public interface ReportService {

    Page<SubmissionReportResponse> getSubmissionReport(
            ReportFilterRequest request,
            int page,
            int size,
            String sortBy,
            String direction);
    ByteArrayInputStream exportSubmissionReport(
            ReportFilterRequest request);
    ByteArrayInputStream exportSubmissionReportExcel(
            ReportFilterRequest request)
            throws IOException;
    void generateDailyReport();

    void generateWeeklyReport();

    void generateMonthlyReport();
}