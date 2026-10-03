package com.example.qcollect.report.util;

import com.example.qcollect.report.dto.SubmissionReportResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelReportExporter {

    public ByteArrayInputStream export(
            List<SubmissionReportResponse> reports)
            throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Submission Report");

        Font headerFont =
                workbook.createFont();

        headerFont.setBold(true);

        CellStyle headerStyle =
                workbook.createCellStyle();

        headerStyle.setFont(headerFont);

        Row header =
                sheet.createRow(0);

        String[] columns = {
                "Submission ID",
                "Project",
                "Form",
                "Accessor",
                "Quality Checker",
                "Status",
                "Submitted At",
                "Reviewed At"
        };

        for (int i = 0; i < columns.length; i++) {

            Cell cell =
                    header.createCell(i);

            cell.setCellValue(columns[i]);

            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;

        for (SubmissionReportResponse report : reports) {

            Row row =
                    sheet.createRow(rowIndex++);

            row.createCell(0)
                    .setCellValue(
                            String.valueOf(report.getSubmissionId()));

            row.createCell(1)
                    .setCellValue(report.getProjectName());

            row.createCell(2)
                    .setCellValue(report.getFormName());

            row.createCell(3)
                    .setCellValue(report.getAccessorName());

            row.createCell(4)
                    .setCellValue(
                            report.getQualityCheckerName());

            row.createCell(5)
                    .setCellValue(
                            report.getStatus().name());

            row.createCell(6)
                    .setCellValue(
                            String.valueOf(report.getSubmittedAt()));

            row.createCell(7)
                    .setCellValue(
                            String.valueOf(report.getReviewedAt()));
        }

        for (int i = 0; i < columns.length; i++) {

            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return new ByteArrayInputStream(
                out.toByteArray());
    }
}