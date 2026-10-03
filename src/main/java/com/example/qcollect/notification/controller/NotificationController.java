package com.example.qcollect.notification.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.notification.dto.BroadcastNotificationRequest;
import com.example.qcollect.notification.dto.CreateNotificationRequest;
import com.example.qcollect.notification.dto.NotificationResponse;
import com.example.qcollect.notification.dto.UnreadCountResponse;
import com.example.qcollect.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Create notification
     */
    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(

            @Valid
            @RequestBody CreateNotificationRequest request) {

        NotificationResponse response =
                notificationService.createNotification(request);

        return ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification created successfully")
                .data(response)
                .build();
    }

    /**
     * Current user's notifications
     */
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(

            Authentication authentication) {

        UUID userId =
                UUID.fromString(authentication.getName());

        List<NotificationResponse> response =
                notificationService.getMyNotifications(userId);

        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(response)
                .build();
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> markAsRead(

            @PathVariable UUID notificationId,

            Authentication authentication) {

        UUID userId =
                UUID.fromString(authentication.getName());

        NotificationResponse response =
                notificationService.markAsRead(
                        notificationId,
                        userId);

        return ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification marked as read")
                .data(response)
                .build();
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(

            Authentication authentication) {

        UUID userId =
                UUID.fromString(authentication.getName());

        notificationService.markAllAsRead(userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("All notifications marked as read")
                .build();
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(

            @PathVariable UUID notificationId,

            Authentication authentication) {

        UUID userId =
                UUID.fromString(authentication.getName());

        notificationService.deleteNotification(
                notificationId,
                userId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification deleted successfully")
                .build();
    }

    /**
     * Get unread notification count
     */
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount(

            Authentication authentication) {

        UUID userId =
                UUID.fromString(authentication.getName());

        UnreadCountResponse response =
                notificationService.getUnreadCount(userId);

        return ApiResponse.<UnreadCountResponse>builder()
                .success(true)
                .message("Unread notification count fetched successfully")
                .data(response)
                .build();
    }
    @PostMapping("/broadcast")
    public ApiResponse<Void> broadcast(
            @Valid
            @RequestBody BroadcastNotificationRequest request) {

        notificationService.broadcast(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Notification sent successfully")
                .build();
    }
}