package com.example.qcollect.mobile.service;

import com.example.qcollect.mobile.dto.*;

import java.util.List;
import java.util.UUID;

public interface MobileService {

    List<MobileFormResponse> getAssignedForms(
            UUID loggedInUserId
    );
    MobileSurveyResponse downloadSurvey(
            UUID formId,
            UUID loggedInUserId
    );
    CreateDraftResponse createDraftSubmission(
            UUID formId,
            UUID loggedInUserId,
            Double latitude,
            Double longitude
    );
    SyncSubmissionResponse syncSubmission(
            SyncSubmissionRequest request,
            UUID loggedInUserId
    );
    void deleteSubmission(
            UUID submissionId,
            UUID loggedInUserId
    );
}