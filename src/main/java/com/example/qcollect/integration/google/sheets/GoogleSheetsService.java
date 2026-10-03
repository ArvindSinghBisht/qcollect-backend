package com.example.qcollect.integration.google.sheets;

import java.util.UUID;

public interface GoogleSheetsService {

    void appendSubmission(UUID submissionId);

}