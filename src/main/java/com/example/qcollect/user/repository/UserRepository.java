package com.example.qcollect.user.repository;

import com.example.qcollect.role.entity.Role;
import com.example.qcollect.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "systemRole")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(UUID id);

    boolean existsBySystemRole_Name(String roleName);

    boolean existsBySystemRole(Role role);
}