package com.example.qcollect.sync.service;

import com.example.qcollect.sync.dto.DownloadSyncResponse;
import com.example.qcollect.sync.dto.SyncLogResponse;
import com.example.qcollect.sync.dto.SyncResponse;
import com.example.qcollect.sync.dto.UploadSyncRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SyncService {

    /**
     * Upload offline submissions from mobile.
     */
    SyncResponse uploadSync(
            UploadSyncRequest request,
            UUID loggedInUserId);

    /**
     * Download latest projects/forms.
     */
    DownloadSyncResponse downloadSync(
            UUID loggedInUserId,
            LocalDateTime lastSync);

    /**
     * Get sync history of current user.
     */
    List<SyncLogResponse> getSyncLogs(
            UUID loggedInUserId);
}