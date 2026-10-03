package com.example.qcollect.project.dto;


import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private UUID id;

    private UUID tenantId;

    private String name;

    private String description;

    private Boolean active;
}