package com.example.qcollect.report.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.report.dto.ReportFilterRequest;
import com.example.qcollect.report.dto.SubmissionReportResponse;
import com.example.qcollect.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/submissions")
    public ApiResponse<Page<SubmissionReportResponse>> getSubmissionReport(
            @Valid

            @RequestBody ReportFilterRequest request,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(defaultValue = "submittedAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        Page<SubmissionReportResponse> response =
                reportService.getSubmissionReport(
                        request,
                        page,
                        size,
                        sortBy,
                        direction);

        return ApiResponse.<Page<SubmissionReportResponse>>builder()
                .success(true)
                .message("Submission report generated successfully")
                .data(response)
                .build();
    }
    @PostMapping("/submissions/export")
    public ResponseEntity<InputStreamResource> exportReport(

            @Valid
            @RequestBody ReportFilterRequest request) {

        ByteArrayInputStream stream =
                reportService.exportSubmissionReport(request);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=submission-report.csv")

                .contentType(MediaType.APPLICATION_OCTET_STREAM)

                .body(new InputStreamResource(stream));
    }
    @PostMapping("/submissions/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel(
            @Valid

            @RequestBody ReportFilterRequest request)
            throws IOException {

        ByteArrayInputStream stream =
                reportService.exportSubmissionReportExcel(
                        request);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=submission-report.xlsx")

                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .body(
                        new InputStreamResource(stream));
    }
}