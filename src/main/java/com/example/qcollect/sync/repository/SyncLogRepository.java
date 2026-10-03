package com.example.qcollect.sync.repository;

import com.example.qcollect.sync.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
public interface SyncLogRepository
        extends JpaRepository<SyncLog, UUID> {

    List<SyncLog> findByUser_IdOrderByCreatedAtDesc(
            UUID userId);


    long deleteByCreatedAtBefore(LocalDateTime dateTime);
}