package com.example.qcollect.project.repository;


//package com.qcollect.project.repository;

import com.example.qcollect.project.entity.Project;
//import com.qcollect.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByTenantId(UUID tenantId);

    boolean existsByTenantIdAndName(UUID tenantId, String name);
    List<Project> findByUpdatedAtAfter(
            LocalDateTime updatedAt);
}