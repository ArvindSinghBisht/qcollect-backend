package com.example.qcollect.project.mapper;

import com.example.qcollect.project.dto.ProjectResponse;
import com.example.qcollect.project.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {

        if (project == null) {
            return null;
        }

        return ProjectResponse.builder()
                .id(project.getId())
                .tenantId(project.getTenantId())
                .name(project.getName())
                .description(project.getDescription())
                .active(project.getActive())
                .build();
    }
}