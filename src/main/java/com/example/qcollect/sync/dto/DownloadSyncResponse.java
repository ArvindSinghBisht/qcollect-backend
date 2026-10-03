package com.example.qcollect.sync.dto;

import com.example.qcollect.form.dto.FormResponse;
import com.example.qcollect.project.dto.ProjectResponse;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadSyncResponse {

    private List<ProjectResponse> projects;

    private List<FormResponse> forms;
    private List<SubmissionPhotoSyncResponse> photos;
    private List<SubmissionAnswerSyncResponse> answers;
    private List<SubmissionReviewSyncResponse> reviews;
    private List<UUID> deletedSubmissions;

    private List<UUID> deletedAnswers;

    private List<UUID> deletedPhotos;
}