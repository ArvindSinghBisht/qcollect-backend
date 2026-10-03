package com.example.qcollect.form.dto;

import com.example.qcollect.formfield.dto.FormFieldResponse;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormPreviewResponse {

    private UUID id;

    private String name;

    private String description;

    private List<FormFieldResponse> fields;
    private String surveyJson;
    private Integer version;
    private Boolean published;
}