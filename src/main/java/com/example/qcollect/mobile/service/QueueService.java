package com.example.qcollect.mobile.service;

import com.example.qcollect.mobile.dto.QueueRequest;
import com.example.qcollect.mobile.dto.QueueResponse;
import com.example.qcollect.mobile.entity.SyncStatus;

import java.util.List;
import java.util.UUID;

public interface QueueService {

    QueueResponse addToQueue(
            QueueRequest request,
            UUID loggedInUserId
    );

    List<QueueResponse> getUserQueue(
            UUID loggedInUserId
    );

    List<QueueResponse> getPendingQueue();

    void updateStatus(
            UUID queueId,
            SyncStatus status
    );

    void markFailed(
            UUID queueId,
            String errorMessage
    );

    void markSuccess(
            UUID queueId
    );
}