package com.example.qcollect.sync.dto;

import com.example.qcollect.sync.enums.SyncConflictStrategy;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSyncRequest {

    private String deviceId;

    private List<UploadSubmissionDto> submissions;
    private SyncConflictStrategy conflictStrategy;
    private List<UploadPhotoDto> photos;
    private List<UploadAnswerDto> answers;

}