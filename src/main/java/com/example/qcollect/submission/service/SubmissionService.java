package com.example.qcollect.submission.service;

import com.example.qcollect.submission.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SubmissionService {

    SubmissionResponse createSubmission(
            UUID formId,
            UUID loggedInUserId,
            Double latitude,
            Double longitude
    );

    void saveAnswers(
            UUID submissionId,
            SubmissionAnswersRequest request,
            UUID loggedInUserId
    );
    List<SubmissionFlaggedAnswerResponse> getFlaggedAnswers(
            UUID submissionId,
            UUID loggedInUserId
    );
    SubmissionAnswersResponse getAnswers(UUID submissionId);

    SubmissionPhotoResponse uploadPhoto(
            UUID submissionId,
            MultipartFile file,
            Double latitude,
            Double longitude,
            UUID loggedInUserId
    );

    SubmissionResponse submitSubmission(
            UUID submissionId,
            UUID loggedInUserId
    );

    List<SubmissionResponse> getPendingSubmissions(
            UUID projectId
    );

    List<SubmissionResponse> getPendingSubmissionsForQualityChecker(
            UUID loggedInUserId
    );

    List<SubmissionResponse> getMySubmissions(
            UUID loggedInUserId
    );

    SubmissionResponse reviewSubmission(
            UUID submissionId,
            QualityCheckRequest request,
            UUID loggedInUserId
    );

    SubmissionResponse getSubmission(
            UUID submissionId
    );

    SubmissionAnswerReviewResponse reviewAnswer(
            UUID answerId,
            ReviewAnswerRequest request,
            UUID loggedInUserId
    );

    SubmissionPhotoReviewResponse reviewPhoto(
            UUID photoId,
            ReviewPhotoRequest request,
            UUID loggedInUserId
    );

    List<SubmissionPhotoResponse> getSubmissionPhotos(
            UUID submissionId
    );

    SubmissionSurveyResponse getSurvey(
            UUID submissionId,
            UUID loggedInUserId
    );

    void deletePhoto(
            UUID photoId,
            UUID loggedInUserId
    );
}