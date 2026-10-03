package com.example.qcollect.submission.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.form.entity.Form;
import com.example.qcollect.form.repository.FormRepository;
import com.example.qcollect.formassignment.entity.FormAssignment;
import com.example.qcollect.formassignment.repository.FormAssignmentRepository;
import com.example.qcollect.formfield.repository.FormFieldRepository;
import com.example.qcollect.formversion.entity.FormVersion;
import com.example.qcollect.formversion.repository.FormVersionRepository;
import com.example.qcollect.integration.cloudinary.CloudinaryStorageService;
import com.example.qcollect.integration.cloudinary.CloudinaryUploadResult;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.notification.service.NotificationHelper;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.project.service.ProjectAuthorizationService;
import com.example.qcollect.qcdelegation.service.EffectiveQcResolver;
import com.example.qcollect.storage.PhotoStorageService;
import com.example.qcollect.submission.dto.*;
import com.example.qcollect.submission.entity.*;
import com.example.qcollect.submission.enums.PhotoUploadStatus;
import com.example.qcollect.submission.enums.ReviewStatus;
import com.example.qcollect.submission.enums.SubmissionStatus;
import com.example.qcollect.submission.mapper.SubmissionAnswerMapper;
import com.example.qcollect.submission.mapper.SubmissionAnswerReviewMapper;
import com.example.qcollect.submission.mapper.SubmissionMapper;
import com.example.qcollect.submission.mapper.SubmissionPhotoMapper;
import com.example.qcollect.submission.repository.SubmissionAnswerRepository;
import com.example.qcollect.submission.repository.SubmissionAnswerReviewRepository;
import com.example.qcollect.submission.repository.SubmissionPhotoRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    private final EffectiveQcResolver effectiveQcResolver;
    private final SubmissionRepository submissionRepository;
    private final FormFieldRepository formFieldRepository;
    private final FormRepository formRepository;
    private final ProjectUserRepository projectUserRepository;
    private final NotificationHelper notificationHelper;
    private final PhotoStorageService photoStorageService;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final AuditHelper auditHelper;
    private final UserRepository userRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final SubmissionAnswerMapper submissionAnswerMapper;
    private final SubmissionAnswerReviewRepository submissionAnswerReviewRepository;
    private final SubmissionPhotoRepository submissionPhotoRepository;
    private final FormVersionRepository formVersionRepository;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final SubmissionPhotoMapper submissionPhotoMapper;
    private final SubmissionAnswerReviewMapper submissionAnswerReviewMapper;
    private final FormAssignmentRepository formAssignmentRepository;
//    private final FormAssignmentRepository formAssignmentRepository;
@Override
public SubmissionResponse createSubmission(
        UUID formId,
        UUID loggedInUserId,
        Double latitude,
        Double longitude
) {

    Form form = formRepository.findByIdAndActiveTrue(formId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Form not found"));

    FormVersion publishedVersion =
            formVersionRepository
                    .findTopByForm_IdAndPublishedTrueOrderByVersionDesc(formId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No published version found"));

    UUID projectId = form.getProject().getId();

    /*
     * User must be an Accessor in this project
     */
    projectAuthorizationService.requireAccessor(
            projectId,
            loggedInUserId
    );

    /*
     * Validate Accessor is assigned to this Form
     * Also load the Assignment because we'll need
     * the assigned QC.
     */
    FormAssignment assignment =
            formAssignmentRepository
                    .findByForm_IdAndAccessor_IdAndActiveTrue(
                            formId,
                            loggedInUserId
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "You are not assigned to this form."
                            ));

    User accessor = userRepository.findById(loggedInUserId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    /*
     * Automatically assign the QC from Form Assignment
     */
    Submission submission = Submission.builder()

            .form(form)

            .formVersion(publishedVersion)

            .accessor(accessor)

            .qualityChecker(
                    assignment.getQualityChecker()
            )

            .status(SubmissionStatus.DRAFT)

            .latitude(latitude)

            .longitude(longitude)

            .answersJson(null)

            .build();

    submission.setCreatedBy(loggedInUserId);

    submission.setUpdatedBy(loggedInUserId);

    submissionRepository.save(submission);

    auditHelper.log(
            loggedInUserId,
            "SUBMISSION",
            "CREATE",
            submission.getId(),
            "Submission created",
            "SYSTEM");

    notificationHelper.notifyUser(
            accessor.getId(),
            "Submission Created",
            "A new submission has been created.",
            NotificationType.SUBMISSION);

    return submissionMapper.toResponse(submission);
}
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getPendingSubmissions(UUID projectId) {

        return submissionRepository
                .findByForm_Project_IdAndStatus(
                        projectId,
                        SubmissionStatus.SUBMITTED)
                .stream()
                .map(submissionMapper::toResponse)
                .toList();
    }

//    @Override

@Override
@Transactional(readOnly = true)
public List<SubmissionResponse> getPendingSubmissionsForQualityChecker(
        UUID loggedInUserId
) {

    List<Submission> submissions =
            submissionRepository.findByStatus(
                    SubmissionStatus.SUBMITTED
            );

    return submissions.stream()

            .filter(submission -> {

                User effectiveQc =
                        effectiveQcResolver.resolve(submission);

                return effectiveQc != null
                        && effectiveQc.getId()
                        .equals(loggedInUserId);

            })

            .map(submissionMapper::toResponse)

            .toList();
}
    //    @Transactional(readOnly = true)
    @Override
    public SubmissionResponse reviewSubmission(
            UUID submissionId,
            QualityCheckRequest request,
            UUID loggedInUserId
    ) {

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Submission not found"));

        UUID projectId = submission.getForm().getProject().getId();

        projectAuthorizationService.requireQualityChecker(
                projectId,
                loggedInUserId
        );
        /*
         * Only assigned QC can review
         */
        User effectiveQc =
                effectiveQcResolver.resolve(submission);

        if (effectiveQc == null
                || !effectiveQc.getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "This submission is not assigned to you."
            );

        }
        User qc = userRepository
                .findById(loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        submission.setStatus(request.getStatus());
        submission.setRemarks(request.getRemarks());
        submission.setQualityChecker(qc);
        submission.setCheckedAt(LocalDateTime.now());
        submission.setUpdatedBy(loggedInUserId);

        submissionRepository.save(submission);

        auditHelper.log(
                loggedInUserId,
                "SUBMISSION",
                request.getStatus().name(),
                submission.getId(),
                "Submission reviewed",
                "SYSTEM");

        if (request.getStatus() == SubmissionStatus.APPROVED) {

            notificationHelper.notifyUser(
                    submission.getAccessor().getId(),
                    "Submission Approved",
                    "Your submission has been approved.",
                    NotificationType.SUBMISSION);

        } else if (request.getStatus() == SubmissionStatus.REJECTED) {

            notificationHelper.notifyUser(
                    submission.getAccessor().getId(),
                    "Submission Rejected",
                    "Your submission has been rejected. Please review the remarks.",
                    NotificationType.SUBMISSION);
        }

        return submissionMapper.toResponse(submission);
    }

    @Override
    public SubmissionResponse submitSubmission(
            UUID submissionId,
            UUID loggedInUserId
    ) {

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Submission not found"));

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {
            throw new ResourceNotFoundException(
                    "Only creator can submit this submission");
        }

        List<SubmissionAnswerReview> reviews =
                submissionAnswerReviewRepository
                        .findBySubmissionAnswer_Submission_Id(submissionId);

        for (SubmissionAnswerReview review : reviews) {
            review.setStatus(ReviewStatus.RESOLVED);
            review.setUpdatedBy(loggedInUserId);
        }

        submissionAnswerReviewRepository.saveAll(reviews);

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setUpdatedBy(loggedInUserId);

        submissionRepository.save(submission);

        auditHelper.log(
                loggedInUserId,
                "SUBMISSION",
                "SUBMIT",
                submission.getId(),
                "Submission submitted",
                "SYSTEM");

        notificationHelper.notifyUser(
                submission.getAccessor().getId(),
                "Submission Submitted",
                "Your submission has been submitted successfully.",
                NotificationType.SUBMISSION);

        return submissionMapper.toResponse(submission);
    }
    @Override
    @Transactional
    public SubmissionResponse getSubmission(UUID submissionId) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Submission not found"));

        return submissionMapper.toResponse(submission);
    }

    @Override
    @Transactional
    public SubmissionAnswerReviewResponse reviewAnswer(

            UUID answerId,

            ReviewAnswerRequest request,

            UUID loggedInUserId) {

        SubmissionAnswer answer =
                submissionAnswerRepository.findById(answerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Answer not found"));

        Submission submission = answer.getSubmission();

        UUID projectId =
                submission.getForm()
                        .getProject()
                        .getId();

        projectAuthorizationService.requireQualityChecker(
                projectId,
                loggedInUserId
        );

        /*
         * Only the assigned QC can review this submission.
         */
        User effectiveQc =
                effectiveQcResolver.resolve(submission);

        if (effectiveQc == null
                || !effectiveQc.getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "This submission is not assigned to you."
            );
        }

        User qc =
                userRepository.findById(loggedInUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found"));

        SubmissionAnswerReview review =
                submissionAnswerReviewRepository
                        .findBySubmissionAnswer_Id(answerId)
                        .orElse(new SubmissionAnswerReview());

        review.setSubmissionAnswer(answer);

        review.setStatus(request.getStatus());

        review.setComment(request.getComment());

        review.setReviewedBy(qc);

        review.setReviewedAt(LocalDateTime.now());

        if (review.getId() == null) {
            review.setCreatedBy(loggedInUserId);
        }

        review.setUpdatedBy(loggedInUserId);

        submissionAnswerReviewRepository.save(review);

        if (request.getStatus() == ReviewStatus.FLAGGED) {

            submission.setStatus(SubmissionStatus.NEEDS_CORRECTION);

            submission.setQualityChecker(qc);

            submission.setCheckedAt(LocalDateTime.now());

            submission.setUpdatedBy(loggedInUserId);

            submissionRepository.save(submission);

            notificationHelper.notifyUser(
                    submission.getAccessor().getId(),
                    "Answer Flagged",
                    "One or more answers require correction.",
                    NotificationType.SUBMISSION
            );
        }

        auditHelper.log(
                loggedInUserId,
                "ANSWER",
                "REVIEW",
                answer.getId(),
                "Answer reviewed",
                "SYSTEM"
        );

        return submissionAnswerReviewMapper.toResponse(review);
    }
    @Override
    public SubmissionPhotoResponse uploadPhoto(
            UUID submissionId,
            MultipartFile file,
            Double latitude,
            Double longitude,
            UUID loggedInUserId
    ) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Submission not found"));

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {
            throw new ResourceNotFoundException(
                    "Only submission owner can upload photos");
        }

        UUID projectId = submission.getForm().getProject().getId();

        CloudinaryUploadResult uploaded = cloudinaryStorageService.upload(
                file,
                projectId,
                submissionId
        );

        SubmissionPhoto photo = SubmissionPhoto.builder()
                .submission(submission)
                .fileName(uploaded.originalFileName())
                // For Cloudinary-backed photos, filePath stores the Cloudinary public_id.
                .filePath(uploaded.publicId())
                .photoUrl(uploaded.secureUrl())
                .latitude(latitude)
                .longitude(longitude)
                .synced(true)
                .syncedAt(LocalDateTime.now())
                .uploadStatus(PhotoUploadStatus.UPLOADED)
                .deleted(false)
                .syncVersion(1)
                .build();

        photo.setCreatedBy(loggedInUserId);
        photo.setUpdatedBy(loggedInUserId);

        submissionPhotoRepository.save(photo);

        auditHelper.log(
                loggedInUserId,
                "PHOTO",
                "UPLOAD",
                photo.getId(),
                "Photo uploaded",
                "SYSTEM"
        );

        return submissionPhotoMapper.toResponse(photo);
    }

    @Override
    public SubmissionPhotoReviewResponse reviewPhoto(
            UUID photoId,
            ReviewPhotoRequest request,
            UUID loggedInUserId
    ) {

        SubmissionPhoto photo = submissionPhotoRepository.findById(photoId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Photo not found"));
//
//        UUID projectId = photo.getSubmission()
//                .getForm()
//                .getProject()
//                .getId();
//
//        projectAuthorizationService.requireQualityChecker(
//                projectId,
//                loggedInUserId
//        );
//
//        User qc = userRepository.findById(loggedInUserId)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("User not found"));
        Submission submission = photo.getSubmission();

        UUID projectId = submission
                .getForm()
                .getProject()
                .getId();

        projectAuthorizationService.requireQualityChecker(
                projectId,
                loggedInUserId
        );

        /*
         * Only assigned QC can review
         */
        User effectiveQc =
                effectiveQcResolver.resolve(submission);

        if (effectiveQc == null
                || !effectiveQc.getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "This submission is not assigned to you."
            );
        }

        User qc = userRepository.findById(loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
        SubmissionPhotoReview review = photo.getReview();

        if (review == null) {
            review = SubmissionPhotoReview.builder()
                    .submissionPhoto(photo)
                    .build();
        }

        review.setStatus(request.getStatus());
        review.setRemarks(request.getRemarks());
//        review.setComment(request.getRemarks());

        review.setReviewedBy(qc);
        review.setReviewedAt(LocalDateTime.now());

        if (review.getId() == null) {
            review.setCreatedBy(loggedInUserId);
        }

        review.setUpdatedBy(loggedInUserId);

        photo.setReview(review);

        submissionPhotoRepository.save(photo);

        auditHelper.log(
                loggedInUserId,
                "PHOTO",
                "REVIEW",
                photo.getId(),
                "Photo reviewed",
                "SYSTEM"
        );

        return SubmissionPhotoReviewResponse.builder()
                .id(review.getId())
                .photoId(photo.getId())
                .status(review.getStatus())
                .remarks(review.getRemarks())

                .reviewedBy(qc.getId())
                .reviewedAt(review.getReviewedAt())
                .build();
    }
    @Override
    @Transactional
    public void saveAnswers(

            UUID submissionId,

            SubmissionAnswersRequest request,

            UUID loggedInUserId
    ) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "Only submission owner can save answers");
        }

        submission.setAnswersJson(request.getAnswersJson());

        // If QC had flagged this submission, editing it
        // brings it back to Draft before re-submission.

        if (submission.getStatus() == SubmissionStatus.NEEDS_CORRECTION) {
            submission.setStatus(SubmissionStatus.DRAFT);
        }

        submission.setAnswersJson(request.getAnswersJson());
        submission.setUpdatedBy(loggedInUserId);
        submission.setUpdatedAt(LocalDateTime.now());

        submissionRepository.save(submission);

        submission.setUpdatedBy(loggedInUserId);

        submission.setUpdatedAt(LocalDateTime.now());

        submissionRepository.save(submission);

        auditHelper.log(

                loggedInUserId,

                "SUBMISSION",

                "SAVE_ANSWERS",

                submission.getId(),

                "Survey answers saved",

                "SYSTEM");
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionAnswersResponse getAnswers(
            UUID submissionId
    ) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        return SubmissionAnswersResponse.builder()

                .submissionId(submission.getId())

                .answersJson(submission.getAnswersJson())

                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionPhotoResponse> getSubmissionPhotos(
            UUID submissionId
    ) {

        return submissionPhotoRepository
                .findBySubmission_IdAndDeletedFalse(submissionId)
                .stream()
                .map(submissionPhotoMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public SubmissionSurveyResponse getSurvey(

            UUID submissionId,

            UUID loggedInUserId
    ) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found"));

        UUID projectId = submission.getForm().getProject().getId();

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {

            projectAuthorizationService.requireQualityChecker(
                    projectId,
                    loggedInUserId
            );

            User effectiveQc =
                    effectiveQcResolver.resolve(submission);

            if (effectiveQc == null
                    || !effectiveQc.getId().equals(loggedInUserId)) {

                throw new ResourceNotFoundException(
                        "This submission is not assigned to you."
                );
            }
        }

        List<SubmissionFlaggedQuestionResponse> flaggedQuestions =
                submissionAnswerReviewRepository
                        .findBySubmissionAnswer_Submission_Id(submissionId)
                        .stream()
                        .map(review -> SubmissionFlaggedQuestionResponse.builder()

                                .answerId(review.getSubmissionAnswer().getId())

                                .fieldId(review.getSubmissionAnswer().getField().getId())

                                .status(review.getStatus())

                                .comment(review.getComment())

                                .build())
                        .toList();

        return SubmissionSurveyResponse.builder()

                .submissionId(submission.getId())

                .formId(submission.getForm().getId())

                .surveyJson(
                        submission.getFormVersion().getSurveyJson()
                )

                .answersJson(
                        submission.getAnswersJson()
                )

                .flaggedQuestions(flaggedQuestions)

                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions(UUID loggedInUserId) {

        return submissionRepository
                .findByAccessor_IdAndDeletedFalseOrderByCreatedAtDesc(loggedInUserId)
                .stream()
                .map(submissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deletePhoto(

            UUID photoId,

            UUID loggedInUserId
    ) {

        SubmissionPhoto photo =
                submissionPhotoRepository.findById(photoId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Photo not found"));

        if (!photo.getSubmission()
                .getAccessor()
                .getId()
                .equals(loggedInUserId)) {

            throw new ResourceNotFoundException(
                    "Only submission owner can delete photo");
        }

        try {
            if (photo.getPhotoUrl() != null && !photo.getPhotoUrl().isBlank()) {
                cloudinaryStorageService.delete(photo.getFilePath());
            } else {
                // Backward compatibility for photos uploaded before Cloudinary integration.
                photoStorageService.delete(photo.getFilePath());
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Unable to delete stored image",
                    ex
            );
        }

        photo.setDeleted(true);

        photo.setDeletedAt(LocalDateTime.now());

        photo.setUpdatedBy(loggedInUserId);

        submissionPhotoRepository.save(photo);

        auditHelper.log(

                loggedInUserId,

                "PHOTO",

                "DELETE",

                photo.getId(),

                "Photo deleted",

                "SYSTEM");
    }
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionFlaggedAnswerResponse> getFlaggedAnswers(
            UUID submissionId,
            UUID loggedInUserId
    ) {

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Submission not found"));

        UUID projectId = submission.getForm().getProject().getId();

        // Accessor can view his own flagged answers.
        // QC can also view them.

        if (!submission.getAccessor().getId().equals(loggedInUserId)) {

            projectAuthorizationService.requireQualityChecker(
                    projectId,
                    loggedInUserId
            );

            User effectiveQc =
                    effectiveQcResolver.resolve(submission);

            if (effectiveQc == null
                    || !effectiveQc.getId().equals(loggedInUserId)) {

                throw new ResourceNotFoundException(
                        "This submission is not assigned to you."
                );
            }
        }

        List<SubmissionAnswerReview> reviews =
                submissionAnswerReviewRepository
                        .findBySubmissionAnswer_Submission_IdAndStatus(
                                submissionId,
                                ReviewStatus.FLAGGED
                        );

        return reviews.stream()
                .map(review -> SubmissionFlaggedAnswerResponse.builder()
                        .answerId(review.getSubmissionAnswer().getId())
                        .formFieldId(review.getSubmissionAnswer().getField().getId())
                        .status(review.getStatus())
                        .remarks(review.getComment())

                        .reviewedBy(review.getReviewedBy().getId())
                        .reviewedAt(review.getReviewedAt())
                        .build())
                .toList();
    }
}