package com.example.qcollect.mobile.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.mobile.dto.CreateDraftResponse;
import com.example.qcollect.mobile.dto.MobileFormResponse;
import com.example.qcollect.mobile.dto.MobileSurveyResponse;
import com.example.qcollect.mobile.dto.SyncSubmissionRequest;
import com.example.qcollect.mobile.dto.SyncSubmissionResponse;
import com.example.qcollect.mobile.service.MobileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileController {

    private final MobileService mobileService;

    @GetMapping("/forms")
    public ApiResponse<List<MobileFormResponse>> getForms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        return ApiResponse.<List<MobileFormResponse>>builder()
                .success(true)
                .message("Assigned forms fetched successfully.")
                .data(mobileService.getAssignedForms(userId))
                .build();
    }

    @GetMapping("/forms/{formId}")
    public ApiResponse<MobileSurveyResponse> downloadSurvey(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        return ApiResponse.<MobileSurveyResponse>builder()
                .success(true)
                .message("Survey downloaded successfully.")
                .data(mobileService.downloadSurvey(formId, userId))
                .build();
    }

    @PostMapping("/forms/{formId}/draft")
    public ApiResponse<CreateDraftResponse> createDraft(
            @PathVariable UUID formId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        return ApiResponse.<CreateDraftResponse>builder()
                .success(true)
                .message("Draft created successfully.")
                .data(mobileService.createDraftSubmission(formId, userId, latitude, longitude))
                .build();
    }

    @PostMapping("/sync")
    public ApiResponse<SyncSubmissionResponse> syncSubmission(
            @RequestBody SyncSubmissionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        return ApiResponse.<SyncSubmissionResponse>builder()
                .success(true)
                .message("Submission synced successfully.")
                .data(mobileService.syncSubmission(request, userId))
                .build();
    }

    @DeleteMapping("/{submissionId}")
    public ApiResponse<Void> deleteSubmission(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        mobileService.deleteSubmission(submissionId, userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Submission deleted successfully.")
                .build();
    }
}