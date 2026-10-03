package com.example.qcollect.scheduler;

import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.notification.service.NotificationHelper;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.enums.SubmissionStatus;
import com.example.qcollect.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubmissionReminderScheduler {

    private final SubmissionRepository submissionRepository;

    private final NotificationHelper notificationHelper;

    /**
     * Runs every day at 9:00 AM.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void remindDraftSubmissions() {

        try {

            log.info("Running draft submission reminder scheduler...");

            List<Submission> drafts =
                    submissionRepository.findByStatus(
                            SubmissionStatus.DRAFT);

            for (Submission submission : drafts) {

                notificationHelper.notifyUser(
                        submission.getAccessor().getId(),
                        "Draft Submission Reminder",
                        "You have a draft submission pending for form: "
                                + submission.getForm().getName(),
                        NotificationType.SUBMISSION);
            }

            log.info(
                    "Draft reminder completed. {} notifications sent.",
                    drafts.size());

        } catch (Exception ex) {

            log.error(
                    "Draft submission reminder scheduler failed.",
                    ex);
        }
    }

    /**
     * Runs every day at 10:00 AM.
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void remindPendingReviews() {

        try {

            log.info("Running pending review reminder scheduler...");

            List<Submission> pending =
                    submissionRepository.findByStatus(
                            SubmissionStatus.SUBMITTED);

            for (Submission submission : pending) {

                if (submission.getQualityChecker() != null) {

                    notificationHelper.notifyUser(
                            submission.getQualityChecker().getId(),
                            "Pending Review Reminder",
                            "Submission pending review: "
                                    + submission.getForm().getName(),
                            NotificationType.SUBMISSION);
                }
            }

            log.info(
                    "Pending review reminder completed. {} submissions checked.",
                    pending.size());

        } catch (Exception ex) {

            log.error(
                    "Pending review reminder scheduler failed.",
                    ex);
        }
    }
}