package com.example.qcollect.form.repository;

import com.example.qcollect.form.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {

    List<Form> findByProject_IdAndActiveTrue(UUID projectId);

    Optional<Form> findByIdAndActiveTrue(UUID id);

    List<Form> findByUpdatedAtAfter(LocalDateTime updatedAt);

    List<Form> findByProject_IdInAndActiveTrue(List<UUID> projectIds);
}