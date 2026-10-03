package com.example.qcollect.notification.service;

import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.notification.dto.BroadcastNotificationRequest;
import com.example.qcollect.notification.dto.CreateNotificationRequest;
import com.example.qcollect.notification.dto.NotificationResponse;
import com.example.qcollect.notification.dto.UnreadCountResponse;
import com.example.qcollect.notification.entity.Notification;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.notification.mapper.NotificationMapper;
import com.example.qcollect.notification.repository.NotificationRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(
            CreateNotificationRequest request) {

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found"));

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .type(request.getType())
                        .isRead(false)
                        .build();

        notification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(notification);
    }
    @Override
    public List<NotificationResponse> getMyNotifications(
            UUID loggedInUserId) {

        return notificationRepository
                .findByUser_IdOrderByCreatedAtDesc(loggedInUserId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
    @Override
    public NotificationResponse markAsRead(
            UUID notificationId,
            UUID loggedInUserId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        if (!notification.getUser().getId().equals(loggedInUserId)) {
            throw new ResourceNotFoundException(
                    "Notification does not belong to current user");
        }

        notification.setIsRead(true);

        notification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(notification);
    }
    @Override
    public void markAllAsRead(
            UUID loggedInUserId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUser_IdOrderByCreatedAtDesc(
                                loggedInUserId);

        notifications.forEach(notification ->
                notification.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }
    @Override
    public void deleteNotification(
            UUID notificationId,
            UUID loggedInUserId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        if (!notification.getUser().getId().equals(loggedInUserId)) {
            throw new ResourceNotFoundException(
                    "Notification does not belong to current user");
        }

        notificationRepository.delete(notification);
    }
    @Override
    public UnreadCountResponse getUnreadCount(
            UUID loggedInUserId) {

        long count =
                notificationRepository
                        .countByUser_IdAndIsReadFalse(
                                loggedInUserId);

        return UnreadCountResponse.builder()
                .count(count)
                .build();
    }
    @Override
    public void broadcast(
            BroadcastNotificationRequest request) {

        List<User> users = userRepository.findAll();

        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .type(NotificationType.SYSTEM)
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}