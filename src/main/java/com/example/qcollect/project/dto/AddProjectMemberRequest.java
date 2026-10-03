package com.example.qcollect.project.dto;

import com.example.qcollect.project.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddProjectMemberRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private ProjectRole role;
}
