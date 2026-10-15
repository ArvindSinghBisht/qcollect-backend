package com.example.qcollect.user.repository;

import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "systemRole")
    @Query("""
            SELECT u
            FROM User u
            WHERE lower(trim(u.email)) = lower(trim(:email))
            """)
    List<User> findAllByNormalizedEmail(@Param("email") String email);

    default Optional<User> findByEmail(String email) {
        List<User> matches = findAllByNormalizedEmail(email);
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        if (matches.size() > 1) {
            throw new BadRequestException(
                    "Multiple accounts match this email. Resolve the duplicate emails before signing in."
            );
        }
        return Optional.of(matches.get(0));
    }

    default boolean existsByEmail(String email) {
        return !findAllByNormalizedEmail(email).isEmpty();
    }

    Optional<User> findById(UUID id);

    boolean existsBySystemRole_Name(String roleName);

    boolean existsBySystemRole(Role role);
}
