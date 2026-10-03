package com.example.qcollect.submission.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.submission.dto.CreateSubmissionRequest;
import com.example.qcollect.submission.dto.QualityCheckRequest;
import com.example.qcollect.submission.dto.ReviewAnswerRequest;
import com.example.qcollect.submission.dto.ReviewPhotoRequest;
import com.example.qcollect.submission.dto.SubmissionAnswerReviewResponse;
import com.example.qcollect.submission.dto.SubmissionAnswersRequest;
import com.example.qcollect.submission.dto.SubmissionAnswersResponse;
import com.example.qcollect.submission.dto.SubmissionPhotoResponse;
import com.example.qcollect.submission.dto.SubmissionPhotoReviewResponse;
import com.example.qcollect.submission.dto.SubmissionResponse;
import com.example.qcollect.submission.dto.SubmissionSurveyResponse;
import com.example.qcollect.submission.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/forms/{formId}")
    public ApiResponse<SubmissionResponse> createSubmission(
            @PathVariable UUID formId,
            @Valid @RequestBody CreateSubmissionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        SubmissionResponse response = submissionService.createSubmission(
                formId,
                loggedInUserId,
                request.getLatitude(),
                request.getLongitude()
        );

        return ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Submission created successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{submissionId}/answers")
    public ApiResponse<SubmissionAnswersResponse> saveAnswers(
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmissionAnswersRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        submissionService.saveAnswers(submissionId, request, loggedInUserId);

        return ApiResponse.<SubmissionAnswersResponse>builder()
                .success(true)
                .message("Answers saved successfully")
                .data(submissionService.getAnswers(submissionId))
                .build();
    }

    @GetMapping("/pending")
    public ApiResponse<List<SubmissionResponse>> getPendingForQualityChecker(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        return ApiResponse.<List<SubmissionResponse>>builder()
                .success(true)
                .message("Pending submissions")
                .data(submissionService.getPendingSubmissionsForQualityChecker(loggedInUserId))
                .build();
    }

    @GetMapping("/projects/{projectId}/pending")
    public ApiResponse<List<SubmissionResponse>> getPending(
            @PathVariable UUID projectId
    ) {
        return ApiResponse.<List<SubmissionResponse>>builder()
                .success(true)
                .message("Pending submissions")
                .data(submissionService.getPendingSubmissions(projectId))
                .build();
    }

    @PutMapping("/{submissionId}/review")
    public ApiResponse<SubmissionResponse> review(
            @PathVariable UUID submissionId,
            @Valid @RequestBody QualityCheckRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        SubmissionResponse response = submissionService.reviewSubmission(
                submissionId,
                request,
                loggedInUserId
        );

        return ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Submission reviewed successfully")
                .data(response)
                .build();
    }

    @PostMapping(
            value = "/{submissionId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<SubmissionPhotoResponse> uploadPhoto(
            @PathVariable UUID submissionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        SubmissionPhotoResponse response = submissionService.uploadPhoto(
                submissionId,
                file,
                latitude,
                longitude,
                userId
        );

        return ApiResponse.<SubmissionPhotoResponse>builder()
                .success(true)
                .message("Photo uploaded successfully")
                .data(response)
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<SubmissionResponse>> getMySubmissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        return ApiResponse.<List<SubmissionResponse>>builder()
                .success(true)
                .message("My submissions fetched successfully")
                .data(submissionService.getMySubmissions(loggedInUserId))
                .build();
    }

    @GetMapping("/{submissionId}")
    public ApiResponse<SubmissionResponse> getSubmission(
            @PathVariable UUID submissionId
    ) {
        return ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Submission fetched successfully")
                .data(submissionService.getSubmission(submissionId))
                .build();
    }

    @GetMapping("/{submissionId}/answers")
    public ApiResponse<SubmissionAnswersResponse> getAnswers(
            @PathVariable UUID submissionId
    ) {
        return ApiResponse.<SubmissionAnswersResponse>builder()
                .success(true)
                .message("Answers fetched successfully")
                .data(submissionService.getAnswers(submissionId))
                .build();
    }

    @GetMapping("/{submissionId}/photos")
    public ApiResponse<List<SubmissionPhotoResponse>> getPhotos(
            @PathVariable UUID submissionId
    ) {
        return ApiResponse.<List<SubmissionPhotoResponse>>builder()
                .success(true)
                .message("Photos fetched successfully")
                .data(submissionService.getSubmissionPhotos(submissionId))
                .build();
    }

    @PostMapping("/answers/{answerId}/review")
    public ApiResponse<SubmissionAnswerReviewResponse> reviewAnswer(
            @PathVariable UUID answerId,
            @Valid @RequestBody ReviewAnswerRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        SubmissionAnswerReviewResponse response = submissionService.reviewAnswer(
                answerId,
                request,
                userId
        );

        return ApiResponse.<SubmissionAnswerReviewResponse>builder()
                .success(true)
                .message("Answer reviewed successfully")
                .data(response)
                .build();
    }

    @PostMapping("/{submissionId}/submit")
    public ApiResponse<SubmissionResponse> submitSubmission(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        SubmissionResponse response = submissionService.submitSubmission(
                submissionId,
                loggedInUserId
        );

        return ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Submission submitted successfully")
                .data(response)
                .build();
    }

    @PostMapping("/photos/{photoId}/review")
    public ApiResponse<SubmissionPhotoReviewResponse> reviewPhoto(
            @PathVariable UUID photoId,
            @Valid @RequestBody ReviewPhotoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID userId = userDetails.getUserId();

        SubmissionPhotoReviewResponse response = submissionService.reviewPhoto(
                photoId,
                request,
                userId
        );

        return ApiResponse.<SubmissionPhotoReviewResponse>builder()
                .success(true)
                .message("Photo reviewed successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{submissionId}/survey")
    public ApiResponse<SubmissionSurveyResponse> getSurvey(
            @PathVariable UUID submissionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        return ApiResponse.<SubmissionSurveyResponse>builder()
                .success(true)
                .message("Survey fetched successfully.")
                .data(submissionService.getSurvey(submissionId, loggedInUserId))
                .build();
    }

    @DeleteMapping("/photos/{photoId}")
    public ApiResponse<String> deletePhoto(
            @PathVariable UUID photoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID loggedInUserId = userDetails.getUserId();

        submissionService.deletePhoto(photoId, loggedInUserId);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Photo deleted successfully")
                .data("SUCCESS")
                .build();
    }

}