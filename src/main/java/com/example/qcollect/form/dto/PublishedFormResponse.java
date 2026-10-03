package com.example.qcollect.form.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PublishedFormResponse {

    private UUID formId;

    private String formName;

    private String description;

    private Integer version;

    private String surveyJson;

}