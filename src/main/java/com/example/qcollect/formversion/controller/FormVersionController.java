package com.example.qcollect.formversion.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.formversion.dto.VersionDetailsResponse;
import com.example.qcollect.formversion.dto.VersionResponse;
import com.example.qcollect.formversion.service.FormVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forms/{formId}/versions")
public class FormVersionController {

    private final FormVersionService formVersionService;

    /**
     * Get all versions of a form
     */
    @GetMapping
    public ApiResponse<List<VersionResponse>> getVersions(
            @PathVariable UUID formId
    ) {

        return ApiResponse.<List<VersionResponse>>builder()
                .success(true)
                .message("Versions fetched successfully.")
                .data(formVersionService.getVersions(formId))
                .build();
    }

    /**
     * Get one specific version
     */
    @GetMapping("/{version}")
    public ApiResponse<VersionDetailsResponse> getVersion(
            @PathVariable UUID formId,
            @PathVariable Integer version
    ) {

        return ApiResponse.<VersionDetailsResponse>builder()
                .success(true)
                .message("Version fetched successfully.")
                .data(formVersionService.getVersion(formId, version))
                .build();
    }

    /**
     * Publish (rollback to) a version
     */
    @PostMapping("/{version}/publish")
    public ApiResponse<VersionDetailsResponse> publishVersion(

            @PathVariable UUID formId,

            @PathVariable Integer version,

            Authentication authentication
    ) {

        UUID loggedInUserId =
                UUID.fromString(authentication.getName());

        return ApiResponse.<VersionDetailsResponse>builder()
                .success(true)
                .message("Version published successfully.")
                .data(
                        formVersionService.publishVersion(
                                formId,
                                version,
                                loggedInUserId
                        )
                )
                .build();
    }

}