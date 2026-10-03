package com.example.qcollect.form.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishedSurveyResponse {

    private UUID formId;

    private String formName;

    private Integer version;

    private String surveyJson;
}