package com.example.qcollect.mobile.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.exception.ConflictException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formversion.entity.FormVersion;
import com.example.qcollect.formversion.repository.FormVersionRepository;
import com.example.qcollect.integration.cloudinary.CloudinaryStorageService;
import com.example.qcollect.integration.cloudinary.CloudinaryUploadResult;
import com.example.qcollect.mobile.dto.*;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.submission.dto.SubmissionResponse;
import com.example.qcollect.submission.entity.Submission;
import com.example.qcollect.submission.entity.SubmissionPhoto;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import com.example.qcollect.submission.repository.SubmissionPhotoRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import com.example.qcollect.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MobileServiceImpl implements MobileService {

    private final FormRepository formRepository;

    private final FormVersionRepository formVersionRepository;

    private final ProjectUserRepository projectUserRepository;

    private final SubmissionRepository submissionRepository;

    private final SubmissionPhotoRepository submissionPhotoRepository;

    private final SubmissionService submissionService;

    private final CloudinaryStorageService cloudinaryStorageService;

    private final AuditHelper auditHelper;

    @Override
    @Transactional(readOnly = true)
    public List<MobileFormResponse> getAssignedForms(UUID loggedInUserId) {

        List<UUID> projectIds =
                projectUserRepository.findProjectIdsByUserIdAndRoleName(
                        loggedInUserId,
                        "ACCESSOR"
                );

        return formRepository
                .findByProject_IdInAndActiveTrue(projectIds)
                .stream()
                .flatMap(form -> formVersionRepository
                        .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(form.getId())
                        .stream()
                        .map(version -> MobileFormResponse.builder()
                                .formId(form.getId())
                                .formName(form.getName())
                                .projectId(form.getProject().getId())
                                .projectName(form.getProject().getName())
                                .version(version.getVersion())
                                .build()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MobileSurveyResponse downloadSurvey(
            UUID formId,
            UUID loggedInUserId
    ) {

        Form form = formRepository
                .findByIdAndActiveTrue(formId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Form not found"));

        boolean assigned = projectUserRepository
                .findProjectIdsByUserIdAndRoleName(
                        loggedInUserId,
                        "ACCESSOR"
                )
                .stream()
                .anyMatch(projectId -> projectId.equals(form.getProject().getId()));

        if (!assigned) {
            throw new ResourceNotFoundException(
                    "You are not assigned to this project as Accessor");
        }

        FormVersion version =
                formVersionRepository
                        .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Published version not found"));

        return MobileSurveyResponse.builder()
                .formId(form.getId())
                .projectId(form.getProject().getId())
                .formName(form.getName())
                .version(version.getVersion())
                .surveyJson(version.getSurveyJson())
                .build();
    }

    @Override
    public CreateDraftResponse createDraftSubmission(
            UUID formId,
            UUID loggedInUserId,
            Double latitude,
            Double longitude
    ) {

        SubmissionResponse submission =
                submissionService.createSubmission(
                        formId,
                        loggedInUserId,
                        latitude,
                        longitude
                );

        FormVersion version =
                formVersionRepository
                        .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(formId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Published version not found"));

        return CreateDraftResponse.builder()
                .submissionId(submission.getId())
                .formId(formId)
                .formVersion(version.getVersion())
                .status(submission.getStatus())
                .build();
    }

    @Override
    @Transactional
    public SyncSubmissionResponse syncSubmission(

            SyncSubmissionRequest request,

            UUID loggedInUserId
    ) {

        Submission submission =
                submissionRepository.findById(request.getSubmissionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "Not your submission");
        }

        /*
         * ------------------------------
         * Version Conflict Detection
         * ------------------------------
         */

        if (!submission.getSyncVersion().equals(request.getSyncVersion())) {

            throw new ConflictException(
                    "Submission has been modified on the server. Please refresh and try again."
            );
        }
        if (Boolean.TRUE.equals(request.getDeleted())) {

            submission.setDeleted(true);

            submission.setDeletedAt(LocalDateTime.now());

        } else {

            submission.setDeleted(false);

            submission.setDeletedAt(null);

        }
        /*
         * Save latest answers
         */

        submission.setAnswersJson(request.getAnswersJson());

        /*
         * Sync metadata
         */

        submission.setSynced(true);

        submission.setSyncedAt(LocalDateTime.now());

        submission.setSyncVersion(
                submission.getSyncVersion() + 1
        );

        submission.setUpdatedBy(loggedInUserId);

        submissionRepository.save(submission);

        /*
         * Save Photos
         */

        if (request.getPhotos() != null) {

            for (SyncPhotoRequest photo : request.getPhotos()) {

                if (Boolean.TRUE.equals(photo.getDeleted())) {
                    continue;
                }

                String imageData = photo.getImage();

                // Backward-compatible fallback when a client sends a data URI
                // in filePath instead of the new image property. A device-local
                // path cannot be read by the backend and is therefore ignored.
                if ((imageData == null || imageData.isBlank())
                        && photo.getFilePath() != null
                        && photo.getFilePath().startsWith("data:image/")) {
                    imageData = photo.getFilePath();
                }

                if (imageData == null || imageData.isBlank()) {
                    continue;
                }

                UUID mobilePhotoId = UUID.randomUUID();

                CloudinaryUploadResult uploadedPhoto =
                        cloudinaryStorageService.uploadBase64(
                                imageData,
                                submission.getForm().getProject().getId(),
                                submission.getId(),
                                mobilePhotoId
                        );

                SubmissionPhoto entity = SubmissionPhoto.builder()
                        .submission(submission)
                        .mobilePhotoId(mobilePhotoId)
                        .fileName(uploadedPhoto.originalFileName())
                        // Cloudinary public_id is stored in filePath for deletion.
                        .filePath(uploadedPhoto.publicId())
                        .photoUrl(uploadedPhoto.secureUrl())
                        .latitude(photo.getLatitude())
                        .longitude(photo.getLongitude())
                        .deleted(false)
                        .synced(true)
                        .syncedAt(LocalDateTime.now())
                        .uploadStatus(PhotoUploadStatus.UPLOADED)
                        .syncVersion(submission.getSyncVersion())
                        .build();

                entity.setCreatedBy(loggedInUserId);
                entity.setUpdatedBy(loggedInUserId);

                submissionPhotoRepository.save(entity);
            }
        }

        auditHelper.log(

                loggedInUserId,

                "SUBMISSION",

                "SYNC",

                submission.getId(),

                "Submission synced from mobile",

                "SYSTEM"
        );

        return SyncSubmissionResponse.builder()

                .submissionId(submission.getId())

                .synced(true)

                .syncVersion(submission.getSyncVersion())

                .build();
    }
    @Override
    @Transactional
    public void deleteSubmission(

            UUID submissionId,

            UUID loggedInUserId
    ) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "Not your submission");
        }

        submission.setDeleted(true);

        submission.setDeletedAt(LocalDateTime.now());

        submission.setSynced(false);

        submission.setSyncVersion(
                submission.getSyncVersion() + 1
        );

        submission.setUpdatedBy(loggedInUserId);

        submissionRepository.save(submission);

        auditHelper.log(

                loggedInUserId,

                "SUBMISSION",

                "DELETE",

                submission.getId(),

                "Submission deleted",

                "SYSTEM"
        );
    }
}