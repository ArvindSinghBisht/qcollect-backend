package com.example.qcollect.audit.repository;

import com.example.qcollect.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUser_IdOrderByCreatedAtDesc(
            UUID userId);

    List<AuditLog> findByModuleOrderByCreatedAtDesc(
            String module);

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from,
            LocalDateTime to);

    List<AuditLog> findByUser_IdAndModuleOrderByCreatedAtDesc(
            UUID userId,
            String module);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}