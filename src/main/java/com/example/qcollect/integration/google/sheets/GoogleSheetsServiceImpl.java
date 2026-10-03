package com.example.qcollect.integration.google.sheets;

import com.example.qcollect.integration.google.entity.ProjectGoogleSheet;
import com.example.qcollect.integration.google.repository.ProjectGoogleSheetRepository;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.entity.SubmissionAnswer;
import com.example.qcollect.submission.repository.SubmissionAnswerRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.qcollect.formfield.entity.FormField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleSheetsServiceImpl
        implements GoogleSheetsService {

    private final SubmissionRepository submissionRepository;

    private final SubmissionAnswerRepository submissionAnswerRepository;

    private final ProjectGoogleSheetRepository googleRepository;

    private final RestTemplate restTemplate;

    @Override
    public void appendSubmission(UUID submissionId) {

        // STEP 1
        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new RuntimeException("Submission not found"));

        // STEP 2
        UUID projectId =
                submission.getForm()
                        .getProject()
                        .getId();

        // STEP 3
        ProjectGoogleSheet googleSheet =
                googleRepository.findByProjectId(projectId)
                        .orElseThrow(() ->
                                new RuntimeException("Google Sheet not connected"));

        // STEP 4
        var answers =
                submissionAnswerRepository
                        .findBySubmission_IdOrderById(submissionId);

        /*
         Next step:List<SubmissionAnswer> answers =

         Convert answers into one row
         and append to Google Sheet
        */

    }

}