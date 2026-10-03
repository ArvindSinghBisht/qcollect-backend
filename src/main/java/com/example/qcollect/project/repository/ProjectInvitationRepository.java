package com.example.qcollect.project.repository;

import com.example.qcollect.project.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectInvitationRepository
        extends JpaRepository<ProjectInvitation, UUID> {


    Optional<ProjectInvitation>
    findByToken(String token);


    boolean existsByEmailAndProjectId(
            String email,
            UUID projectId
    );

}