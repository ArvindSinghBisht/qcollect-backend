package com.example.qcollect.tenant.service;

import com.example.qcollect.tenant.dto.CreateTenantAdminRequest;
import com.example.qcollect.tenant.dto.CreateTenantRequest;
import com.example.qcollect.tenant.dto.TenantResponse;

import java.util.UUID;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);
//    TenantResponse createTenant(CreateTenantRequest request);

    void createTenantAdmin(
            UUID tenantId,
            CreateTenantAdminRequest request
    );
}