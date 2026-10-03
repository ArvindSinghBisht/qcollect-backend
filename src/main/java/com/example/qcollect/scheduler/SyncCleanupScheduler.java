package com.example.qcollect.scheduler;

import com.example.qcollect.sync.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncCleanupScheduler {

    private final SyncLogRepository syncLogRepository;

    /**
     * Runs every day at 3:00 AM.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldSyncLogs() {

        try {

            log.info("Sync cleanup started.");

            long deleted =
                    syncLogRepository.deleteByCreatedAtBefore(
                            LocalDateTime.now().minusDays(30));

            log.info(
                    "Sync cleanup completed. Deleted {} sync logs.",
                    deleted);

        } catch (Exception ex) {

            log.error(
                    "Sync cleanup scheduler failed.",
                    ex);
        }
    }
}