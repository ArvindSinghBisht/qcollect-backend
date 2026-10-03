package com.example.qcollect.formversion.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionResponse {

    private UUID id;

    private Integer version;

    private Boolean published;

    private LocalDateTime createdAt;

    private UUID createdBy;
}