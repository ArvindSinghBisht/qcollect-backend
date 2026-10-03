package com.example.qcollect.formfield.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.formfield.dto.*;
import com.example.qcollect.formfield.service.FormFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forms")
public class FormFieldController {

    private final FormFieldService formFieldService;

    @PostMapping("/{formId}/fields")
    public ApiResponse<FormFieldResponse> createField(
            @PathVariable UUID formId,
            @Valid @RequestBody CreateFormFieldRequest request,
            Authentication authentication
    ) {

        UUID loggedInUserId = UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Field created successfully.")
                .data(formFieldService.createField(
                        formId,
                        request,
                        loggedInUserId))
                .build();
    }

    @GetMapping("/{formId}/fields")
    public ApiResponse<List<FormFieldResponse>> getFields(
            @PathVariable UUID formId
    ) {

        return ApiResponse.<List<FormFieldResponse>>builder()
                .success(true)
                .message("Fields fetched successfully.")
                .data(formFieldService.getFields(formId))
                .build();
    }

    @GetMapping("/fields/{fieldId}")
    public ApiResponse<FormFieldResponse> getField(
            @PathVariable UUID fieldId
    ) {

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Field fetched successfully.")
                .data(formFieldService.getField(fieldId))
                .build();
    }

    @PutMapping("/fields/{fieldId}")
    public ApiResponse<FormFieldResponse> updateField(
            @PathVariable UUID fieldId,
            @Valid @RequestBody UpdateFormFieldRequest request,
            Authentication authentication
    ) {

        UUID loggedInUserId = UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Field updated successfully.")
                .data(formFieldService.updateField(
                        fieldId,
                        request,
                        loggedInUserId))
                .build();
    }

    @DeleteMapping("/fields/{fieldId}")
    public ApiResponse<String> deleteField(
            @PathVariable UUID fieldId,
            Authentication authentication
    ) {

        UUID loggedInUserId = UUID.fromString(authentication.getName());

        formFieldService.deleteField(
                fieldId,
                loggedInUserId
        );

        return ApiResponse.<String>builder()
                .success(true)
                .message("Field deleted successfully.")
                .data("OK")
                .build();
    }
    @PutMapping("/{formId}/fields/order")
    public ApiResponse<String> reorderFields(
            @PathVariable UUID formId,
            @RequestBody List<ReorderFieldRequest> request,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        formFieldService.reorderFields(
                formId,
                request,
                loggedInUserId
        );

        return ApiResponse.<String>builder()
                .success(true)
                .message("Fields reordered successfully.")
                .data("OK")
                .build();
    }
    @PostMapping("/fields/{fieldId}/duplicate")
    public ApiResponse<FormFieldResponse> duplicateField(
            @PathVariable UUID fieldId,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Field duplicated successfully.")
                .data(
                        formFieldService.duplicateField(
                                fieldId,
                                loggedInUserId
                        )
                )
                .build();
    }
    @PatchMapping("/fields/{fieldId}/toggle")
    public ApiResponse<FormFieldResponse> toggleFieldStatus(
            @PathVariable UUID fieldId,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Field status updated successfully.")
                .data(
                        formFieldService.toggleFieldStatus(
                                fieldId,
                                loggedInUserId
                        )
                )
                .build();
    }
    @PutMapping("/fields/{fieldId}/validation")
    public ApiResponse<FormFieldResponse> updateValidation(
            @PathVariable UUID fieldId,
            @RequestBody FieldValidationRequest request,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Validation updated successfully.")
                .data(
                        formFieldService.updateValidation(
                                fieldId,
                                request,
                                loggedInUserId
                        )
                )
                .build();
    }
    @PutMapping("/fields/{fieldId}/options")
    public ApiResponse<FormFieldResponse> updateOptions(
            @PathVariable UUID fieldId,
            @RequestBody FieldOptionsRequest request,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<FormFieldResponse>builder()
                .success(true)
                .message("Options updated successfully.")
                .data(
                        formFieldService.updateOptions(
                                fieldId,
                                request,
                                loggedInUserId
                        )
                )
                .build();

    }
    @PostMapping("/{formId}/copy-fields")
    public ApiResponse<String> copyFields(
            @PathVariable UUID formId,
            @RequestBody CopyFieldsRequest request,
            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        formFieldService.copyFields(
                formId,
                request,
                loggedInUserId
        );

        return ApiResponse.<String>builder()
                .success(true)
                .message("Fields copied successfully.")
                .data("OK")
                .build();

    }
}