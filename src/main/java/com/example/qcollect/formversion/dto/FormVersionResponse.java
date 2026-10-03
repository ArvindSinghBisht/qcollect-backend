package com.example.qcollect.formversion.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormVersionResponse {

    private UUID id;

    private UUID formId;

    private Integer version;

    private Boolean published;

    private String surveyJson;

}
