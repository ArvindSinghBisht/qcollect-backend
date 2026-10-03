package com.example.qcollect.project.service;

import java.util.UUID;

public interface ProjectAuthorizationService {

    void requireProjectAdmin(
            UUID projectId,
            UUID userId
    );

    void requireAccessor(
            UUID projectId,
            UUID userId
    );

    void requireQualityChecker(
            UUID projectId,
            UUID userId
    );

    void requireProjectMember(
            UUID projectId,
            UUID userId
    );
}
