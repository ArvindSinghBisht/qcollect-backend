package com.example.qcollect.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardRefreshScheduler {

    /**
     * Refresh dashboard statistics every 15 minutes.
     */
    @Scheduled(cron = "0 */15 * * * *")
    public void refreshDashboardCache() {

        try {

            log.info("Dashboard refresh scheduler started.");

            // TODO:
            // dashboardService.refreshDashboardCache();

            log.info("Dashboard refresh scheduler completed successfully.");

        } catch (Exception ex) {

            log.error(
                    "Dashboard refresh scheduler failed.",
                    ex);
        }
    }
}