package com.example.qcollect.scheduler;

import com.example.qcollect.notification.entity.Notification;
import com.example.qcollect.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * Runs every day at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldNotifications() {

        try {

            log.info("Notification cleanup started.");

            LocalDateTime threshold =
                    LocalDateTime.now().minusDays(30);

            List<Notification> oldNotifications =
                    notificationRepository.findByCreatedAtBefore(
                            threshold);

            if (!oldNotifications.isEmpty()) {
                notificationRepository.deleteAll(oldNotifications);
            }

            log.info(
                    "Notification cleanup completed. Deleted {} notifications.",
                    oldNotifications.size());

        } catch (Exception ex) {

            log.error(
                    "Notification cleanup scheduler failed.",
                    ex);
        }
    }
}