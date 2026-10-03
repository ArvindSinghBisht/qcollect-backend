package com.example.qcollect.form.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.form.dto.PublishedSurveyResponse;
import com.example.qcollect.form.service.FormService;
import com.example.qcollect.submission.dto.SubmissionSurveyResponse;
import com.example.qcollect.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyRuntimeController {

    private final SubmissionService submissionService;
    private final FormService formService;
    @GetMapping("/{submissionId}")
    public ApiResponse<SubmissionSurveyResponse> getSurvey(

            @PathVariable UUID submissionId,

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<SubmissionSurveyResponse>builder()

                .success(true)

                .message("Survey loaded successfully.")

                .data(
                        submissionService.getSurvey(
                                submissionId,
                                loggedInUserId
                        )
                )

                .build();
    }
    @GetMapping("/forms/{formId}")
    public ApiResponse<PublishedSurveyResponse> getPublishedSurvey(

            @PathVariable UUID formId,

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<PublishedSurveyResponse>builder()

                .success(true)

                .message("Published survey loaded successfully.")

                .data(

                        formService.getPublishedSurvey(

                                formId,

                                loggedInUserId
                        )
                )

                .build();
    }
}