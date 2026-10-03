package com.example.qcollect.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank
    private String tenantName;

    @NotBlank
    private String tenantCode;

    private String logoUrl;

}