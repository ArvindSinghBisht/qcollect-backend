package com.example.qcollect.notification.service;

import com.example.qcollect.notification.dto.BroadcastNotificationRequest;
import com.example.qcollect.notification.dto.CreateNotificationRequest;
import com.example.qcollect.notification.dto.NotificationResponse;
import com.example.qcollect.notification.dto.UnreadCountResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationResponse createNotification(
            CreateNotificationRequest request);

    List<NotificationResponse> getMyNotifications(
            UUID loggedInUserId);

    NotificationResponse markAsRead(
            UUID notificationId,
            UUID loggedInUserId);

    void markAllAsRead(
            UUID loggedInUserId);

    void deleteNotification(
            UUID notificationId,
            UUID loggedInUserId);

    UnreadCountResponse getUnreadCount(
            UUID loggedInUserId);

    void broadcast(BroadcastNotificationRequest request);
}