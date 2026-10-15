package com.example.qcollect.project.repository;

import com.example.qcollect.project.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectInvitationRepository
        extends JpaRepository<ProjectInvitation, UUID> {


    Optional<ProjectInvitation>
    findByToken(String token);


    @Query("""
            SELECT CASE WHEN COUNT(invitation) > 0 THEN true ELSE false END
            FROM ProjectInvitation invitation
            WHERE invitation.project.id = :projectId
              AND lower(trim(invitation.email)) = lower(trim(:email))
            """)
    boolean existsByEmailAndProjectId(
            @Param("email") String email,
            @Param("projectId") UUID projectId
    );

}