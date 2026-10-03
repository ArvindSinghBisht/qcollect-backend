package com.example.qcollect.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProjectResponse {

    private UUID projectId;

    private String projectName;

    private UUID roleId;

    private String roleName;
}
