package com.example.qcollect.auth.dto;

import com.example.qcollect.project.dto.ProjectMembershipResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private UUID userId;

    private UUID tenantId;

    private String systemRole;

    private String firstName;

    private String lastName;

    private String email;

    private List<ProjectMembershipResponse> projects;
}
