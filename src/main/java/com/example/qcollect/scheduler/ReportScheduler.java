package com.example.qcollect.scheduler;

import com.example.qcollect.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportScheduler {

    private final ReportService reportService;

    /**
     * Daily report generation.
     * Runs every day at 12:30 AM.
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void generateDailyReport() {

        try {

            log.info("Starting daily report generation...");

            reportService.generateDailyReport();

            log.info("Daily report generated successfully.");

        } catch (Exception ex) {

            log.error(
                    "Failed to generate daily report.",
                    ex);
        }
    }

    /**
     * Weekly report generation.
     * Runs every Monday at 1:00 AM.
     */
    @Scheduled(cron = "0 0 1 * * MON")
    public void generateWeeklyReport() {

        try {

            log.info("Starting weekly report generation...");

            reportService.generateWeeklyReport();

            log.info("Weekly report generated successfully.");

        } catch (Exception ex) {

            log.error(
                    "Failed to generate weekly report.",
                    ex);
        }
    }

    /**
     * Monthly report generation.
     * Runs on the first day of every month at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 1 * *")
    public void generateMonthlyReport() {

        try {

            log.info("Starting monthly report generation...");

            reportService.generateMonthlyReport();

            log.info("Monthly report generated successfully.");

        } catch (Exception ex) {

            log.error(
                    "Failed to generate monthly report.",
                    ex);
        }
    }
}