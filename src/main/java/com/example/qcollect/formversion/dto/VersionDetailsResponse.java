package com.example.qcollect.formversion.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDetailsResponse {

    private UUID id;

    private Integer version;

    private Boolean published;

    private String surveyJson;
}