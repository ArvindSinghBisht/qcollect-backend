package com.example.qcollect.tenant.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.tenant.dto.CreateTenantAdminRequest;
import com.example.qcollect.tenant.dto.CreateTenantRequest;
import com.example.qcollect.tenant.dto.TenantResponse;
import com.example.qcollect.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ApiResponse<TenantResponse> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {

        return ApiResponse.<TenantResponse>builder()
                .success(true)
                .message("Tenant created successfully.")
                .data(tenantService.createTenant(request))
                .build();
    }
    @PostMapping("/{tenantId}/admins")
    public ApiResponse<?> createTenantAdmin(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateTenantAdminRequest request,
            Authentication authentication
    ) {

        System.out.println(authentication);

        tenantService.createTenantAdmin(tenantId, request);

        return ApiResponse.builder()
                .success(true)
                .message("Tenant Admin created successfully.")
                .build();
    }

}