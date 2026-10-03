package com.example.qcollect.report.service;

import com.example.qcollect.report.dto.ReportFilterRequest;
import com.example.qcollect.report.dto.SubmissionReportResponse;
import com.example.qcollect.report.repository.ReportRepository;
import com.example.qcollect.report.specification.SubmissionSpecification;
import com.example.qcollect.report.util.CsvReportExporter;
import com.example.qcollect.report.util.ExcelReportExporter;
import com.example.qcollect.submission.entity.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    private final CsvReportExporter csvReportExporter;
    private final ReportRepository reportRepository;
    private final ExcelReportExporter excelReportExporter;
    @Override
    public Page<SubmissionReportResponse> getSubmissionReport(
            ReportFilterRequest request,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort =
                direction.equalsIgnoreCase("DESC")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Submission> pageResult =
                reportRepository.findAll(
                        SubmissionSpecification.filter(request),
                        pageable);
        List<Submission> submissions =
                pageResult.getContent();

        // Project filter
        if (request.getProjectId() != null) {

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getForm()
                                            .getProject()
                                            .getId()
                                            .equals(request.getProjectId()))
                            .toList();
        }

        // Form filter
        if (request.getFormId() != null) {

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getForm()
                                            .getId()
                                            .equals(request.getFormId()))
                            .toList();
        }

        // Accessor filter
        if (request.getAccessorId() != null) {

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getAccessor()
                                            .getId()
                                            .equals(request.getAccessorId()))
                            .toList();
        }

        // Quality Checker filter
        if (request.getQualityCheckerId() != null) {

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getQualityChecker() != null
                                            && submission.getQualityChecker()
                                            .getId()
                                            .equals(request.getQualityCheckerId()))
                            .toList();
        }

        // Status filter
        if (request.getStatus() != null) {

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getStatus()
                                            == request.getStatus())
                            .toList();
        }

        // From Date filter
        if (request.getFromDate() != null) {

            LocalDateTime from =
                    request.getFromDate().atStartOfDay();

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getSubmittedAt() != null
                                            && !submission.getSubmittedAt()
                                            .isBefore(from))
                            .toList();
        }

        // To Date filter
        if (request.getToDate() != null) {

            LocalDateTime to =
                    request.getToDate().atTime(23, 59, 59);

            submissions =
                    submissions.stream()
                            .filter(submission ->
                                    submission.getSubmittedAt() != null
                                            && !submission.getSubmittedAt()
                                            .isAfter(to))
                            .toList();
        }

        List<SubmissionReportResponse> response =
                submissions.stream()

                        .map(submission ->

                                SubmissionReportResponse.builder()

                                        .submissionId(
                                                submission.getId())

                                        .projectName(
                                                submission.getForm()
                                                        .getProject()
                                                        .getName())

                                        .formName(
                                                submission.getForm()
                                                        .getName())

                                        .accessorName(
                                                submission.getAccessor()
                                                        .getFirstName()
                                                        + " "
                                                        + submission.getAccessor()
                                                        .getLastName())

                                        .qualityCheckerName(
                                                submission.getQualityChecker() == null
                                                        ? null
                                                        : submission.getQualityChecker()
                                                        .getFirstName()
                                                        + " "
                                                        + submission.getQualityChecker()
                                                        .getLastName())

                                        .status(
                                                submission.getStatus())

                                        .submittedAt(
                                                submission.getSubmittedAt())

                                        .reviewedAt(
                                                submission.getCheckedAt())

                                        .build())

                        .toList();

        return new PageImpl<>(
                response,
                pageable,
                pageResult.getTotalElements());
    }
    @Override
    public ByteArrayInputStream exportSubmissionReport(
            ReportFilterRequest request) {

        List<SubmissionReportResponse> reports =

                getSubmissionReport(
                        request,
                        0,
                        Integer.MAX_VALUE,
                        "submittedAt",
                        "DESC")
                        .getContent();

        return csvReportExporter.export(reports);
    }
    @Override
    public ByteArrayInputStream exportSubmissionReportExcel(
            ReportFilterRequest request)
            throws IOException {

        List<SubmissionReportResponse> reports =
                getSubmissionReport(
                        request,
                        0,
                        Integer.MAX_VALUE,
                        "submittedAt",
                        "DESC")
                        .getContent();

        return excelReportExporter.export(reports);
    }
    @Override
    public void generateDailyReport() {

        // TODO:
        // Generate today's submission report.
        // Save PDF/Excel if required.
    }
    @Override
    public void generateWeeklyReport() {

        // TODO:
        // Generate last 7 days report.
    }
    @Override
    public void generateMonthlyReport() {

        // TODO:
        // Generate current month's report.
    }
}