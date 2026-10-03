package com.example.qcollect.notification.repository;

import com.example.qcollect.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByUser_IdOrderByCreatedAtDesc(
            UUID userId);

    long countByUser_IdAndIsReadFalse(
            UUID userId);
    List<Notification> findByUser_Id(UUID userId);



    List<Notification> findByCreatedAtBefore(
            LocalDateTime dateTime);
}