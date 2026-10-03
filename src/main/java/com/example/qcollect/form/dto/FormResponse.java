package com.example.qcollect.form.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResponse {

    private UUID id;

    private UUID projectId;

    private String name;

    private String description;

    private Boolean active;

    private String surveyJson;

    private Integer version;

    private Boolean published;
}