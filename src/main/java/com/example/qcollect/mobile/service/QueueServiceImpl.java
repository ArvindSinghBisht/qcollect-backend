package com.example.qcollect.mobile.service;

import com.example.qcollect.audit.service.AuditHelper;
import com.example.qcollect.mobile.dto.QueueRequest;
import com.example.qcollect.mobile.dto.QueueResponse;
import com.example.qcollect.mobile.entity.SyncQueue;
import com.example.qcollect.mobile.entity.SyncStatus;
import com.example.qcollect.mobile.mapper.SyncQueueMapper;
import com.example.qcollect.mobile.repository.SyncQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QueueServiceImpl implements QueueService {

    private final SyncQueueRepository queueRepository;

    private final SyncQueueMapper mapper;

    private final AuditHelper auditHelper;

    @Override
    public QueueResponse addToQueue(

            QueueRequest request,

            UUID loggedInUserId
    ) {

        SyncQueue queue = SyncQueue.builder()

                .submissionId(request.getSubmissionId())

                .userId(loggedInUserId)

                .payload(request.getPayload())

                .status(SyncStatus.PENDING)

                .retryCount(0)

                .build();

        queue.setCreatedBy(loggedInUserId);

        queue.setUpdatedBy(loggedInUserId);

        queueRepository.save(queue);

        auditHelper.log(

                loggedInUserId,

                "SYNC_QUEUE",

                "ADD",

                queue.getId(),

                "Added item to sync queue",

                "SYSTEM"
        );

        return mapper.toResponse(queue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueResponse> getUserQueue(
            UUID loggedInUserId
    ) {

        return queueRepository

                .findByUserIdOrderByCreatedAtDesc(loggedInUserId)

                .stream()

                .map(mapper::toResponse)

                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueueResponse> getPendingQueue() {

        return queueRepository

                .findByStatusOrderByCreatedAtAsc(SyncStatus.PENDING)

                .stream()

                .map(mapper::toResponse)

                .toList();
    }

    @Override
    public void updateStatus(

            UUID queueId,

            SyncStatus status
    ) {

        SyncQueue queue = queueRepository.findById(queueId)
                .orElseThrow();

        queue.setStatus(status);

        queue.setLastAttempt(LocalDateTime.now());

        queueRepository.save(queue);
    }

    @Override
    public void markFailed(

            UUID queueId,

            String errorMessage
    ) {

        SyncQueue queue = queueRepository.findById(queueId)
                .orElseThrow();

        queue.setStatus(SyncStatus.FAILED);

        queue.setRetryCount(queue.getRetryCount() + 1);

        queue.setLastAttempt(LocalDateTime.now());

        queue.setErrorMessage(errorMessage);

        queueRepository.save(queue);
    }

    @Override
    public void markSuccess(
            UUID queueId
    ) {

        SyncQueue queue = queueRepository.findById(queueId)
                .orElseThrow();

        queue.setStatus(SyncStatus.SUCCESS);

        queue.setLastAttempt(LocalDateTime.now());

        queueRepository.save(queue);
    }
}