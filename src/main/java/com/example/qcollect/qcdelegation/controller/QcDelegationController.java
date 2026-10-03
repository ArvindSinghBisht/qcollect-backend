package com.example.qcollect.qcdelegation.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.qcdelegation.dto.CreateQcDelegationRequest;
import com.example.qcollect.qcdelegation.dto.QcDelegationResponse;
import com.example.qcollect.qcdelegation.service.QcDelegationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/qc-delegations")
public class QcDelegationController {

    private final QcDelegationService delegationService;

    @PostMapping
    public ApiResponse<QcDelegationResponse> create(

            @PathVariable UUID projectId,

            @Valid
            @RequestBody
            CreateQcDelegationRequest request,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {

        return ApiResponse.success(

                delegationService.createDelegation(
                        projectId,
                        request,
                        user.getUserId()
                )

        );
    }

    @GetMapping
    public ApiResponse<List<QcDelegationResponse>> list(

            @PathVariable UUID projectId,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {

        return ApiResponse.success(

                delegationService.getDelegations(
                        projectId,
                        user.getUserId()
                )

        );
    }

    @DeleteMapping("/{delegationId}")
    public ApiResponse<Void> delete(

            @PathVariable UUID delegationId,

            @AuthenticationPrincipal
            CustomUserDetails user
    ) {

        delegationService.removeDelegation(
                delegationId,
                user.getUserId()
        );

        return ApiResponse.success(null);
    }
}