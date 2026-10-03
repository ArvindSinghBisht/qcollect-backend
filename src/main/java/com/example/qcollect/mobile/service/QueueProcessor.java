package com.example.qcollect.mobile.service;

import com.example.qcollect.mobile.dto.QueueResponse;
import com.example.qcollect.mobile.entity.SyncStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueProcessor {

    private final QueueService queueService;

//    @Scheduled(fixedDelay = 30000)
@Scheduled(fixedDelayString = "${queue.sync.delay:30000}")

public void processQueue() {

        List<QueueResponse> pendingItems =
                queueService.getPendingQueue();

        if (pendingItems.isEmpty()) {
            return;
        }

        log.info("Processing {} queue item(s)", pendingItems.size());

        for (QueueResponse item : pendingItems) {

            try {

                queueService.updateStatus(
                        item.getId(),
                        SyncStatus.PROCESSING
                );

                /*
                 * Actual submission sync
                 * (Cloud upload/API sync)
                 * will be added later.
                 */

                queueService.markSuccess(
                        item.getId()
                );

            } catch (Exception ex) {

                queueService.markFailed(
                        item.getId(),
                        ex.getMessage()
                );

                log.error(
                        "Queue {} failed",
                        item.getId(),
                        ex
                );
            }

        }

    }

}