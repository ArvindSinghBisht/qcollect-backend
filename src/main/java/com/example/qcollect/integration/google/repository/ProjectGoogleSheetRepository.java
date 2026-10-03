package com.example.qcollect.integration.google.repository;

import com.example.qcollect.integration.google.entity.ProjectGoogleSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectGoogleSheetRepository
        extends JpaRepository<ProjectGoogleSheet, UUID> {

    Optional<ProjectGoogleSheet> findByProjectId(UUID projectId);

}