package com.example.qcollect.mobile.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileSurveyResponse {

    private UUID formId;

    private UUID projectId;

    private String formName;

    private Integer version;

    private String surveyJson;

}