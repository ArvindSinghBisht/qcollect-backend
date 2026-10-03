package com.example.qcollect.project.repository;

import com.example.qcollect.project.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, UUID> {

    List<ProjectUser> findByProjectId(UUID projectId);

    List<ProjectUser> findByProjectIdAndActiveTrue(UUID projectId);

    List<ProjectUser> findAllByProjectId(UUID projectId);

    List<ProjectUser> findByUserId(UUID userId);

    long countByProjectIdAndRoleId(UUID projectId, UUID roleId);

    long countByProjectIdAndRoleIdAndActiveTrue(UUID projectId, UUID roleId);

    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserIdAndActiveTrue(UUID projectId, UUID userId);

    Optional<ProjectUser> findByProjectIdAndUserId(UUID projectId, UUID userId);

    Optional<ProjectUser> findByProjectIdAndUserIdAndActiveTrue(
            UUID projectId,
            UUID userId
    );

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserIdAndRoleId(
            UUID projectId,
            UUID userId,
            UUID roleId
    );

    boolean existsByProjectIdAndUserIdAndRoleIdAndActiveTrue(
            UUID projectId,
            UUID userId,
            UUID roleId
    );

    @Query("""
        SELECT pu
        FROM ProjectUser pu
        JOIN Role r
            ON pu.roleId = r.id
        WHERE pu.projectId = :projectId
          AND pu.userId = :userId
          AND r.name = :role
          AND pu.active = true
          AND r.active = true
    """)
    Optional<ProjectUser> findMembership(
            @Param("projectId") UUID projectId,
            @Param("userId") UUID userId,
            @Param("role") String role
    );

    @Query("""
        SELECT pu.projectId
        FROM ProjectUser pu
        JOIN Project p
            ON pu.projectId = p.id
        JOIN Role r
            ON pu.roleId = r.id
        WHERE pu.userId = :userId
          AND r.name = :roleName
          AND pu.active = true
          AND p.active = true
          AND r.active = true
    """)
    List<UUID> findProjectIdsByUserIdAndRoleName(
            @Param("userId") UUID userId,
            @Param("roleName") String roleName
    );

    @Query("""
        SELECT
            p.id AS projectId,
            p.name AS projectName,
            r.id AS roleId,
            r.name AS roleName
        FROM ProjectUser pu
        JOIN Project p
            ON pu.projectId = p.id
        JOIN Role r
            ON pu.roleId = r.id
        WHERE pu.userId = :userId
          AND pu.active = true
          AND p.active = true
          AND r.active = true
        ORDER BY p.name ASC
    """)
    List<ProjectMembershipProjection> findActiveMembershipsByUserId(
            @Param("userId") UUID userId
    );
}
