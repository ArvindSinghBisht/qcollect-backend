package com.example.qcollect.project.dto;



import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProjectMemberResponse {

    private UUID id;

    private UUID userId;

    private String userName;

    private String email;

    private String role;

}