package com.example.qcollect.audit.service;

import com.example.qcollect.audit.dto.AuditLogResponse;
import com.example.qcollect.audit.dto.CreateAuditLogRequest;
import com.example.qcollect.audit.entity.AuditLog;
import com.example.qcollect.audit.mapper.AuditLogMapper;
import com.example.qcollect.audit.repository.AuditLogRepository;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl
        implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private final UserRepository userRepository;

    private final AuditLogMapper auditLogMapper;

    @Override
    public AuditLogResponse createAuditLog(
            CreateAuditLogRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .module(request.getModule())
                .action(request.getAction())
                .entityId(request.getEntityId())
                .description(request.getDescription())
                .ipAddress(request.getIpAddress())
                .build();

        auditLog.setCreatedBy(request.getUserId());
        auditLog.setUpdatedBy(request.getUserId());

        auditLog = auditLogRepository.save(auditLog);

        return auditLogMapper.toResponse(auditLog);
    }

    @Override
    public List<AuditLogResponse> getAllLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getUserLogs(
            UUID userId) {

        return auditLogRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getModuleLogs(
            String module) {

        return auditLogRepository
                .findByModuleOrderByCreatedAtDesc(module)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsBetween(
            LocalDateTime from,
            LocalDateTime to) {

        return auditLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getUserModuleLogs(
            UUID userId,
            String module) {

        return auditLogRepository
                .findByUser_IdAndModuleOrderByCreatedAtDesc(
                        userId,
                        module)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }
}