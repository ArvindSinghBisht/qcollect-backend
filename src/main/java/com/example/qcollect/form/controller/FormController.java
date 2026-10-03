package com.example.qcollect.form.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.form.dto.BuilderSaveRequest;
import com.example.qcollect.form.dto.CreateFormRequest;
import com.example.qcollect.form.dto.FormBuilderResponse;
import com.example.qcollect.form.dto.FormPreviewResponse;
import com.example.qcollect.form.dto.FormResponse;
import com.example.qcollect.form.dto.PublishedFormResponse;
import com.example.qcollect.form.dto.SaveDraftRequest;
import com.example.qcollect.form.dto.UpdateFormRequest;
import com.example.qcollect.form.service.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @PostMapping("/projects/{projectId}/forms")
    public ApiResponse<FormResponse> createForm(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateFormRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormResponse>builder()
                .success(true)
                .message("Form created successfully.")
                .data(formService.createForm(projectId, request, user.getUserId()))
                .build();
    }

    @GetMapping("/projects/{projectId}/forms")
    public ApiResponse<List<FormResponse>> getForms(
            @PathVariable UUID projectId
    ) {
        return ApiResponse.<List<FormResponse>>builder()
                .success(true)
                .message("Forms fetched successfully.")
                .data(formService.getForms(projectId))
                .build();
    }

    @GetMapping("/forms/{formId}")
    public ApiResponse<FormResponse> getForm(
            @PathVariable UUID formId
    ) {
        return ApiResponse.<FormResponse>builder()
                .success(true)
                .message("Form fetched successfully.")
                .data(formService.getForm(formId))
                .build();
    }

    @PutMapping("/forms/{formId}")
    public ApiResponse<FormResponse> updateForm(
            @PathVariable UUID formId,
            @Valid @RequestBody UpdateFormRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormResponse>builder()
                .success(true)
                .message("Form updated successfully.")
                .data(formService.updateForm(formId, request, user.getUserId()))
                .build();
    }

    @DeleteMapping("/forms/{formId}")
    public ApiResponse<String> deleteForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        formService.deleteForm(formId, user.getUserId());

        return ApiResponse.<String>builder()
                .success(true)
                .message("Form deleted successfully.")
                .data("SUCCESS")
                .build();
    }

    @GetMapping("/forms/{formId}/preview")
    public ApiResponse<FormPreviewResponse> previewForm(
            @PathVariable UUID formId
    ) {
        return ApiResponse.<FormPreviewResponse>builder()
                .success(true)
                .message("Form preview fetched successfully.")
                .data(formService.previewForm(formId))
                .build();
    }

    @PatchMapping("/forms/{formId}/publish")
    public ApiResponse<FormResponse> publishForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormResponse>builder()
                .success(true)
                .message("Form published successfully.")
                .data(formService.publishForm(formId, user.getUserId()))
                .build();
    }

    @PutMapping("/forms/{formId}/draft")
    public ApiResponse<FormResponse> saveDraft(
            @PathVariable UUID formId,
            @RequestBody @Valid SaveDraftRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormResponse>builder()
                .success(true)
                .message("Draft saved successfully.")
                .data(formService.saveDraft(formId, request, user.getUserId()))
                .build();
    }

    @GetMapping("/forms/{formId}/published")
    public ApiResponse<PublishedFormResponse> getPublishedFormForMember(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<PublishedFormResponse>builder()
                .success(true)
                .message("Published form fetched successfully.")
                .data(formService.getPublishedForm(formId, user.getUserId()))
                .build();
    }

    @GetMapping("/public/forms/{formId}/published")
    public ApiResponse<PublishedFormResponse> getPublishedFormPublic(
            @PathVariable UUID formId
    ) {
        return ApiResponse.<PublishedFormResponse>builder()
                .success(true)
                .message("Published form fetched successfully.")
                .data(formService.getPublishedForm(formId))
                .build();
    }

    @GetMapping("/forms/{formId}/builder")
    public ApiResponse<FormBuilderResponse> getBuilder(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormBuilderResponse>builder()
                .success(true)
                .message("Builder loaded successfully.")
                .data(formService.getBuilder(formId, user.getUserId()))
                .build();
    }

    @PutMapping("/forms/{formId}/builder")
    public ApiResponse<FormBuilderResponse> saveBuilder(
            @PathVariable UUID formId,
            @Valid @RequestBody BuilderSaveRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormBuilderResponse>builder()
                .success(true)
                .message("Builder draft saved successfully.")
                .data(formService.saveBuilder(formId, request.getSurveyJson(), user.getUserId()))
                .build();
    }

    @PostMapping("/forms/{formId}/builder/publish")
    public ApiResponse<FormBuilderResponse> publishBuilder(
            @PathVariable UUID formId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<FormBuilderResponse>builder()
                .success(true)
                .message("Form published successfully.")
                .data(formService.publishBuilder(formId, user.getUserId()))
                .build();
    }
    @GetMapping("/my")
    public ApiResponse<List<FormResponse>> getAssignedForms(

            @AuthenticationPrincipal
            CustomUserDetails user

    ) {

        return ApiResponse.<List<FormResponse>>builder()

                .success(true)

                .message("Assigned forms fetched successfully.")

                .data(

                        formService.getAssignedForms(
                                user.getUserId()
                        )

                )

                .build();

    }
}
