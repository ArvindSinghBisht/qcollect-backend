package com.example.qcollect.mobile.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileFormResponse {

    private UUID formId;

    private String formName;

    private UUID projectId;

    private String projectName;

    private Integer version;
}