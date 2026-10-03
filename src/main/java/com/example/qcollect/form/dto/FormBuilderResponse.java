package com.example.qcollect.form.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormBuilderResponse {

    private UUID formId;

    private String formName;

    private Integer version;

    private Boolean published;

    private String surveyJson;

}