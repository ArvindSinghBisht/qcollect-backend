package com.example.qcollect.integration.google.service;

import com.example.qcollect.formfield.entity.FormField;
import com.example.qcollect.integration.google.dto.GoogleAppendRequest;
import com.example.qcollect.integration.google.entity.ProjectGoogleSheet;
import com.example.qcollect.integration.google.repository.ProjectGoogleSheetRepository;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.entity.SubmissionAnswer;
import com.example.qcollect.submission.repository.SubmissionAnswerRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleSheetServiceImpl
        implements GoogleSheetService {

    private final SubmissionRepository submissionRepository;

    private final SubmissionAnswerRepository submissionAnswerRepository;

    private final ProjectGoogleSheetRepository googleRepository;

    private final RestTemplate restTemplate;

    @Override
    public void appendSubmission(UUID submissionId) {

        // -----------------------------------------
        // Load Submission
        // -----------------------------------------
        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new RuntimeException("Submission not found"));

        // -----------------------------------------
        // Load Project
        // -----------------------------------------
        UUID projectId =
                submission.getForm()
                        .getProject()
                        .getId();

        // -----------------------------------------
        // Load Google Configuration
        // -----------------------------------------
        ProjectGoogleSheet googleSheet =
                googleRepository.findByProjectId(projectId)
                        .orElseThrow(() ->
                                new RuntimeException("Google Sheet not connected"));

        // -----------------------------------------
        // Ensure Header Exists
        // -----------------------------------------
        ensureHeaders(
                googleSheet,
                submission
        );

        // -----------------------------------------
        // Load Submission Answers
        // -----------------------------------------
        List<SubmissionAnswer> answers =
                submissionAnswerRepository
                        .findBySubmission_IdOrderById(submissionId);

        // -----------------------------------------
        // Build Google Sheet Row
        // -----------------------------------------
        List<Object> row = new ArrayList<>();

        row.add(submission.getId().toString());

        row.add(submission.getAccessor().getFirstName());

        row.add(submission.getAccessor().getLastName());

        row.add(submission.getSubmittedAt());

        row.add(submission.getLatitude());

        row.add(submission.getLongitude());

        row.add(submission.getStatus().name());

        for (SubmissionAnswer answer : answers) {

            row.add(answer.getValue());

        }

        // -----------------------------------------
        // Google Append Payload
        // -----------------------------------------
        GoogleAppendRequest appendRequest =
                new GoogleAppendRequest();

        appendRequest.setValues(
                List.of(row)
        );

        // -----------------------------------------
        // HTTP Headers
        // -----------------------------------------
        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(
                googleSheet.getAccessToken()
        );

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        HttpEntity<GoogleAppendRequest> entity =
                new HttpEntity<>(
                        appendRequest,
                        headers
                );

        // -----------------------------------------
        // Google Append URL
        // -----------------------------------------
        String appendUrl =
                "https://sheets.googleapis.com/v4/spreadsheets/"
                        + googleSheet.getSpreadsheetId()
                        + "/values/"
                        + googleSheet.getSheetName()
                        + ":append?valueInputOption=RAW";

        // -----------------------------------------
        // Append Row
        // -----------------------------------------
        restTemplate.exchange(
                appendUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

    }

    private void ensureHeaders(
            ProjectGoogleSheet googleSheet,
            Submission submission
    ) {
        // -------------------------------------------------------
        // Build Header List
        // -------------------------------------------------------

        List<String> headers = new ArrayList<>();

        headers.add("Submission Id");

        headers.add("Accessor First Name");

        headers.add("Accessor Last Name");

        headers.add("Submitted At");

        headers.add("Latitude");

        headers.add("Longitude");

        headers.add("Status");

        submission.getForm()
                .getFields()
                .stream()
                .sorted(Comparator.comparing(FormField::getFieldOrder))
                .forEach(field ->
                        headers.add(field.getLabel())
                );

        // -------------------------------------------------------
        // Convert Header -> Google Format
        // -------------------------------------------------------

        List<List<Object>> values = new ArrayList<>();

        values.add(
                new ArrayList<>(headers)
        );

        Map<String, Object> body =
                new HashMap<>();

        body.put("values", values);

        // -------------------------------------------------------
        // HTTP Headers
        // -------------------------------------------------------

        HttpHeaders headersRequest =
                new HttpHeaders();

        headersRequest.setBearerAuth(
                googleSheet.getAccessToken()
        );

        headersRequest.setContentType(
                MediaType.APPLICATION_JSON
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        body,
                        headersRequest
                );

        // -------------------------------------------------------
        // Header URL
        // -------------------------------------------------------

        String headerUrl =
                "https://sheets.googleapis.com/v4/spreadsheets/"
                        + googleSheet.getSpreadsheetId()
                        + "/values/"
                        + googleSheet.getSheetName()
                        + "!A1"
                        + "?valueInputOption=RAW";

        // -------------------------------------------------------
        // Create Header Row
        //
        // NOTE:
        // Currently this executes every time.
        // In the next step we'll first check whether
        // row A1 already exists.
        // -------------------------------------------------------

        restTemplate.exchange(
                headerUrl,
                HttpMethod.PUT,
                entity,
                String.class
        );

    }

}
    