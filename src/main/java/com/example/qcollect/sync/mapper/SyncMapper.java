package com.example.qcollect.sync.mapper;

import com.example.qcollect.sync.dto.SyncLogResponse;
import com.example.qcollect.sync.entity.SyncLog;
import org.springframework.stereotype.Component;

@Component
public class SyncMapper {

    public SyncLogResponse toResponse(SyncLog syncLog) {

        if (syncLog == null) {
            return null;
        }

        return SyncLogResponse.builder()
                .deviceId(syncLog.getDeviceId())
                .uploadedCount(syncLog.getUploadedCount())
                .downloadedCount(syncLog.getDownloadedCount())
                .success(syncLog.getSuccess())
                .message(syncLog.getMessage())
                .createdAt(syncLog.getCreatedAt())
                .build();
    }

}