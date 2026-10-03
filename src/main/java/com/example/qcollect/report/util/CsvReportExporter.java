package com.example.qcollect.report.util;

import com.example.qcollect.report.dto.SubmissionReportResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Component
public class CsvReportExporter {

    public ByteArrayInputStream export(
            List<SubmissionReportResponse> reports) {

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        PrintWriter writer =
                new PrintWriter(out);

        writer.println(
                "Submission ID,Project,Form,Accessor,Quality Checker,Status,Submitted At,Reviewed At");

        for (SubmissionReportResponse report : reports) {

            writer.println(

                    report.getSubmissionId() + "," +

                            report.getProjectName() + "," +

                            report.getFormName() + "," +

                            report.getAccessorName() + "," +

                            report.getQualityCheckerName() + "," +

                            report.getStatus() + "," +

                            report.getSubmittedAt() + "," +

                            report.getReviewedAt());
        }

        writer.flush();

        return new ByteArrayInputStream(out.toByteArray());
    }
}