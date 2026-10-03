package com.example.qcollect.project.repository;

import java.util.UUID;

public interface ProjectMembershipProjection {

    UUID getProjectId();

    String getProjectName();

    UUID getRoleId();

    String getRoleName();
}
