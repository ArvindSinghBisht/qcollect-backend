package com.example.qcollect.tenant.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TenantResponse {

    private UUID id;

    private String name;

    private String code;

    private Boolean active;
}