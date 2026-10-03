package com.example.qcollect.notification.service;

import com.example.qcollect.notification.dto.CreateNotificationRequest;
import com.example.qcollect.notification.enums.NotificationType;
import com.example.qcollect.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationService notificationService;

    public void notifyUser(
            UUID userId,
            String title,
            String message,
            NotificationType type) {

        notificationService.createNotification(

                CreateNotificationRequest.builder()
                        .userId(userId)
                        .title(title)
                        .message(message)
                        .type(type)
                        .build());
    }

    public void notifyUsers(
            List<User> users,
            String title,
            String message,
            NotificationType type) {

        for (User user : users) {

            notifyUser(
                    user.getId(),
                    title,
                    message,
                    type);
        }
    }
}