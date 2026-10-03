package com.example.qcollect.sync.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.sync.dto.DownloadSyncResponse;
import com.example.qcollect.sync.dto.SyncLogResponse;
import com.example.qcollect.sync.dto.SyncResponse;
import com.example.qcollect.sync.dto.UploadSyncRequest;
import com.example.qcollect.sync.service.SyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sync")
public class SyncController {

    private final SyncService syncService;

    /**
     * Upload offline data from mobile
     */
    @PostMapping("/upload")
    public ApiResponse<SyncResponse> uploadSync(

            @Valid
            @RequestBody UploadSyncRequest request,

            Authentication authentication
    ) {

        UUID userId =
                UUID.fromString(authentication.getName());

        SyncResponse response =
                syncService.uploadSync(
                        request,
                        userId);

        return ApiResponse.<SyncResponse>builder()
                .success(true)
                .message("Synchronization upload completed successfully")
                .data(response)
                .build();
    }

    /**
     * Download latest projects/forms
     * If lastSync is provided, only changed data is returned.
     */
    @GetMapping("/download")
    public ApiResponse<DownloadSyncResponse> download(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lastSync,

            Authentication authentication
    ) {

        UUID userId =
                UUID.fromString(authentication.getName());

        DownloadSyncResponse response =
                syncService.downloadSync(
                        userId,
                        lastSync);

        return ApiResponse.<DownloadSyncResponse>builder()
                .success(true)
                .message("Synchronization download completed successfully")
                .data(response)
                .build();
    }

    /**
     * Sync history
     */
    @GetMapping("/logs")
    public ApiResponse<List<SyncLogResponse>> getSyncLogs(

            Authentication authentication
    ) {

        UUID userId =
                UUID.fromString(authentication.getName());

        List<SyncLogResponse> response =
                syncService.getSyncLogs(userId);

        return ApiResponse.<List<SyncLogResponse>>builder()
                .success(true)
                .message("Sync logs fetched successfully")
                .data(response)
                .build();
    }

    /**
     * Sync status
     */
    @GetMapping("/status")
    public ApiResponse<String> status() {

        return ApiResponse.<String>builder()
                .success(true)
                .message("Sync service is running")
                .data("ONLINE")
                .build();
    }

}