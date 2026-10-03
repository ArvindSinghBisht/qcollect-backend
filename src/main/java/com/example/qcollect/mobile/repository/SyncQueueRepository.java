package com.example.qcollect.mobile.repository;

import com.example.qcollect.mobile.entity.SyncQueue;
import com.example.qcollect.mobile.entity.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SyncQueueRepository
        extends JpaRepository<SyncQueue, UUID> {

    List<SyncQueue> findByStatusOrderByCreatedAtAsc(
            SyncStatus status
    );

    List<SyncQueue> findByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    List<SyncQueue> findBySubmissionIdOrderByCreatedAtDesc(
            UUID submissionId
    );

    long countByStatus(
            SyncStatus status
    );
}