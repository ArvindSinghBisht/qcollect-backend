package com.example.qcollect.project.service;

import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.project.enums.ProjectRole;
import com.example.qcollect.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectAuthorizationServiceImpl implements ProjectAuthorizationService {

    private final ProjectUserRepository projectUserRepository;

    @Override
    public void requireProjectAdmin(UUID projectId, UUID userId) {
        requireRole(projectId, userId, ProjectRole.PROJECT_ADMIN);
    }

    @Override
    public void requireAccessor(UUID projectId, UUID userId) {
        requireRole(projectId, userId, ProjectRole.ACCESSOR);
    }

    @Override
    public void requireQualityChecker(UUID projectId, UUID userId) {
        requireRole(projectId, userId, ProjectRole.QUALITY_CHECKER);
    }

    @Override
    public void requireProjectMember(UUID projectId, UUID userId) {
        projectUserRepository
                .findByProjectIdAndUserIdAndActiveTrue(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User is not an active member of this project."
                        ));
    }

    private void requireRole(
            UUID projectId,
            UUID userId,
            ProjectRole role
    ) {
        projectUserRepository.findMembership(
                projectId,
                userId,
                role.name()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Required project role: " + role.name()
                ));
    }
}
