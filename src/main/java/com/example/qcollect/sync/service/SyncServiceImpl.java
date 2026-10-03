package com.example.qcollect.sync.service;

import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.dto.FormResponse;
import com.example.qcollect.form.mapper.FormMapper;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.integration.cloudinary.CloudinaryStorageService;
import com.example.qcollect.integration.cloudinary.CloudinaryUploadResult;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.notification.service.NotificationHelper;
import com.example.qcollect.project.dto.ProjectResponse;
import com.example.qcollect.project.entity.ProjectUser;
import com.example.qcollect.project.repository.ProjectRepository;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.entity.SubmissionAnswer;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import com.example.qcollect.submission.repository.SubmissionAnswerRepository;
import com.example.qcollect.submission.repository.SubmissionPhotoRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import com.example.qcollect.sync.dto.*;
import com.example.qcollect.sync.entity.SyncLog;
import com.example.qcollect.sync.enums.SyncConflictStrategy;
import com.example.qcollect.sync.mapper.SubmissionAnswerSyncMapper;
import com.example.qcollect.sync.mapper.SubmissionPhotoSyncMapper;
import com.example.qcollect.sync.mapper.SubmissionReviewSyncMapper;
import com.example.qcollect.sync.mapper.SyncMapper;
import com.example.qcollect.sync.repository.SyncLogRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncServiceImpl implements SyncService {

    private final SyncLogRepository syncLogRepository;

    private final SubmissionRepository submissionRepository;

    private final SubmissionAnswerRepository submissionAnswerRepository;

    private final SubmissionPhotoRepository submissionPhotoRepository;

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectUserRepository projectUserRepository;

    private final FormRepository formRepository;

    private final FormMapper formMapper;

    private final SyncMapper syncMapper;

    private final SubmissionAnswerSyncMapper submissionAnswerSyncMapper;

    private final SubmissionPhotoSyncMapper submissionPhotoSyncMapper;
    private final NotificationHelper notificationHelper;
    private final SubmissionReviewSyncMapper submissionReviewSyncMapper;

    private final CloudinaryStorageService cloudinaryStorageService;

    @Override
    public SyncResponse uploadSync(
            UploadSyncRequest request,
            UUID loggedInUserId) {

        User user =
                userRepository.findById(loggedInUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found"));

        int uploaded = 0;

        List<SyncConflictDto> conflicts =
                new ArrayList<>();

        // ==========================================
        // Sync Submissions
        // ==========================================

        if (request.getSubmissions() != null) {

            for (UploadSubmissionDto dto : request.getSubmissions()) {

                Submission submission =
                        submissionRepository
                                .findById(dto.getSubmissionId())
                                .orElse(null);

                if (submission == null) {
                    continue;
                }

                Integer serverVersion =
                        submission.getSyncVersion() == null
                                ? 0
                                : submission.getSyncVersion();

                Integer clientVersion =
                        dto.getSyncVersion() == null
                                ? 0
                                : dto.getSyncVersion();

                if (!serverVersion.equals(clientVersion)
                        && request.getConflictStrategy()
                        == SyncConflictStrategy.SERVER_WINS) {

                    conflicts.add(
                            SyncConflictDto.builder()
                                    .submissionId(submission.getId())
                                    .serverVersion(serverVersion)
                                    .clientVersion(clientVersion)
                                    .reason("Submission version conflict")
                                    .build());

                    continue;
                }

                submission.setSynced(true);
                submission.setSyncedAt(LocalDateTime.now());
                submission.setSyncVersion(serverVersion + 1);
                submission.setLastModifiedDevice(request.getDeviceId());
                submission.setUpdatedBy(loggedInUserId);

                submissionRepository.save(submission);

                uploaded++;
            }
        }

        // ==========================================
        // Sync Answers
        // ==========================================
        if (request.getAnswers() != null) {

            for (UploadAnswerDto dto : request.getAnswers()) {

                SubmissionAnswer answer =
                        submissionAnswerRepository
                                .findById(dto.getAnswerId())
                                .orElse(null);

                if (answer == null) {
                    continue;
                }

                Integer serverVersion =
                        answer.getSyncVersion() == null
                                ? 0
                                : answer.getSyncVersion();

                Integer clientVersion =
                        dto.getSyncVersion() == null
                                ? 0
                                : dto.getSyncVersion();

                if (!serverVersion.equals(clientVersion)
                        && request.getConflictStrategy()
                        == SyncConflictStrategy.SERVER_WINS) {

                    conflicts.add(
                            SyncConflictDto.builder()
                                    .submissionId(answer.getSubmission().getId())
                                    .serverVersion(serverVersion)
                                    .clientVersion(clientVersion)
                                    .reason("Answer version conflict")
                                    .build());

                    continue;
                }

                answer.setValue(dto.getValue());

                answer.setSynced(true);
                answer.setSyncedAt(LocalDateTime.now());
                answer.setSyncVersion(serverVersion + 1);
                answer.setLastModifiedDevice(request.getDeviceId());

                answer.setUpdatedBy(loggedInUserId);

                submissionAnswerRepository.save(answer);
            }
        }

        // ==========================================
        // Sync Photos to Cloudinary
        // ==========================================

        if (request.getPhotos() != null) {

            for (UploadPhotoDto dto : request.getPhotos()) {

                Submission submission =
                        submissionRepository
                                .findById(dto.getSubmissionId())
                                .orElse(null);

                if (submission == null) {
                    continue;
                }

                if (!submission.getAccessor().getId().equals(loggedInUserId)) {
                    conflicts.add(
                            SyncConflictDto.builder()
                                    .submissionId(submission.getId())
                                    .reason("Photo does not belong to the logged-in assessor")
                                    .build());
                    continue;
                }

                UUID mobilePhotoId = dto.getPhotoId() == null
                        ? UUID.randomUUID()
                        : dto.getPhotoId();

                SubmissionPhoto photo =
                        submissionPhotoRepository
                                .findByMobilePhotoId(mobilePhotoId)
                                .orElse(null);

                if (!StringUtils.hasText(dto.getImage())) {
                    if (photo != null) {
                        photo.setLatitude(dto.getLatitude());
                        photo.setLongitude(dto.getLongitude());
                        photo.setLastModifiedDevice(request.getDeviceId());
                        photo.setUpdatedBy(loggedInUserId);
                        submissionPhotoRepository.save(photo);
                    } else {
                        conflicts.add(
                                SyncConflictDto.builder()
                                        .submissionId(submission.getId())
                                        .reason("Photo image data is missing")
                                        .build());
                    }
                    continue;
                }

                CloudinaryUploadResult uploadedPhoto =
                        cloudinaryStorageService.uploadBase64(
                                dto.getImage(),
                                submission.getForm().getProject().getId(),
                                submission.getId(),
                                mobilePhotoId
                        );

                if (photo != null
                        && photo.getPhotoUrl() != null
                        && !photo.getPhotoUrl().isBlank()
                        && photo.getFilePath() != null
                        && !photo.getFilePath().isBlank()) {
                    cloudinaryStorageService.delete(photo.getFilePath());
                }

                if (photo == null) {
                    photo = SubmissionPhoto.builder()
                            .mobilePhotoId(mobilePhotoId)
                            .submission(submission)
                            .build();
                    photo.setCreatedBy(loggedInUserId);
                }

                photo.setFileName(uploadedPhoto.originalFileName());
                // Cloudinary public_id is stored in filePath for deletion.
                photo.setFilePath(uploadedPhoto.publicId());
                photo.setPhotoUrl(uploadedPhoto.secureUrl());
                photo.setLatitude(dto.getLatitude());
                photo.setLongitude(dto.getLongitude());
                photo.setDeleted(false);
                photo.setDeletedAt(null);
                photo.setUploadStatus(PhotoUploadStatus.UPLOADED);
                photo.setSynced(true);
                photo.setSyncedAt(LocalDateTime.now());

                Integer version =
                        photo.getSyncVersion() == null
                                ? 0
                                : photo.getSyncVersion();

                photo.setSyncVersion(version + 1);
                photo.setLastModifiedDevice(request.getDeviceId());
                photo.setUpdatedBy(loggedInUserId);

                submissionPhotoRepository.save(photo);
                uploaded++;
            }
        }

        // ==========================================
        // Save Sync Log
        // ==========================================

        SyncLog log = SyncLog.builder()
                .user(user)
                .deviceId(request.getDeviceId())
                .uploadedCount(uploaded)
                .downloadedCount(0)
                .success(true)
                .message(
                        conflicts.isEmpty()
                                ? "Upload sync completed successfully"
                                : "Upload completed with conflicts")
                .conflictCount(conflicts.size())
                .conflictStrategy(
                        request.getConflictStrategy() == null
                                ? SyncConflictStrategy.SERVER_WINS.name()
                                : request.getConflictStrategy().name())
                .conflictSummary(
                        conflicts.stream()
                                .map(conflict ->
                                        conflict.getSubmissionId()
                                                + " -> "
                                                + conflict.getReason())
                                .reduce((a, b) -> a + "\n" + b)
                                .orElse(""))
                .build();

        log.setCreatedBy(loggedInUserId);
        log.setUpdatedBy(loggedInUserId);

        syncLogRepository.save(log);
        if (conflicts.isEmpty()) {

            notificationHelper.notifyUser(
                    loggedInUserId,
                    "Sync Completed",
                    "Your offline data has been synchronized successfully.",
                    NotificationType.SYNC);

        } else {

            notificationHelper.notifyUser(
                    loggedInUserId,
                    "Sync Completed With Conflicts",
                    "Some records could not be synchronized automatically.",
                    NotificationType.SYNC);
        }
        return SyncResponse.builder()
                .uploaded(uploaded)
                .downloaded(0)
                .message(
                        conflicts.isEmpty()
                                ? "Sync upload completed successfully"
                                : "Sync completed with conflicts")
                .conflicts(conflicts)
                .build();
    }
    @Override
    public DownloadSyncResponse downloadSync(
            UUID loggedInUserId,
            LocalDateTime lastSyncTime) {

        // ==========================================
        // Projects
        // ==========================================

        List<ProjectUser> memberships =
                projectUserRepository.findByUserId(loggedInUserId);

        List<ProjectResponse> projects =
                memberships.stream()
                        .map(ProjectUser::getProjectId)
                        .map(projectRepository::findById)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .map(project -> ProjectResponse.builder()
                                .id(project.getId())
                                .name(project.getName())
                                .description(project.getDescription())
                                .build())
                        .toList();

        // ==========================================
        // Forms
        // ==========================================

        List<FormResponse> forms =
                formRepository.findAll()
                        .stream()
                        .map(formMapper::toResponse)
                        .toList();

        // ==========================================
        // Answers
        // ==========================================

        List<SubmissionAnswerSyncResponse> answers =
                lastSyncTime == null
                        ? submissionAnswerRepository.findAll()
                        .stream()
                        .map(submissionAnswerSyncMapper::toResponse)
                        .toList()
                        : submissionAnswerRepository
                        .findByUpdatedAtAfter(lastSyncTime)
                        .stream()
                        .map(submissionAnswerSyncMapper::toResponse)
                        .toList();

        // ==========================================
        // Reviews
        // ==========================================

        List<SubmissionReviewSyncResponse> reviews =
                lastSyncTime == null
                        ? submissionRepository.findAll()
                        .stream()
                        .map(submissionReviewSyncMapper::toResponse)
                        .toList()
                        : submissionRepository
                        .findByUpdatedAtAfter(lastSyncTime)
                        .stream()
                        .map(submissionReviewSyncMapper::toResponse)
                        .toList();

        // ==========================================
        // Photos
        // ==========================================

        List<SubmissionPhotoSyncResponse> photos =
                lastSyncTime == null
                        ? submissionPhotoRepository.findAll()
                        .stream()
                        .map(submissionPhotoSyncMapper::toResponse)
                        .toList()
                        : submissionPhotoRepository
                        .findByUpdatedAtAfter(lastSyncTime)
                        .stream()
                        .map(submissionPhotoSyncMapper::toResponse)
                        .toList();

        // ==========================================
        // Deleted Submissions
        // ==========================================

        List<UUID> deletedSubmissions =
                submissionRepository.findByDeletedTrue()
                        .stream()
                        .map(Submission::getId)
                        .toList();

        // ==========================================
        // Deleted Answers
        // ==========================================

        List<UUID> deletedAnswers =
                submissionAnswerRepository.findByDeletedTrue()
                        .stream()
                        .map(SubmissionAnswer::getId)
                        .toList();

        // ==========================================
        // Deleted Photos
        // ==========================================

        List<UUID> deletedPhotos =
                submissionPhotoRepository.findByDeletedTrue()
                        .stream()
                        .map(SubmissionPhoto::getId)
                        .toList();
        // ==========================================
        // Build Download Response
        // ==========================================

        return DownloadSyncResponse.builder()
                .projects(projects)
                .forms(forms)
                .answers(answers)
                .reviews(reviews)
                .photos(photos)
                .deletedSubmissions(deletedSubmissions)
                .deletedAnswers(deletedAnswers)
                .deletedPhotos(deletedPhotos)
                .build();
    }

    @Override
    public List<SyncLogResponse> getSyncLogs(
            UUID loggedInUserId) {

        return syncLogRepository
                .findByUser_IdOrderByCreatedAtDesc(loggedInUserId)
                .stream()
                .map(syncMapper::toResponse)
                .toList();
    }

}