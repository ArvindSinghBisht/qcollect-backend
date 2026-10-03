package com.example.qcollect.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastNotificationRequest {

    private String title;

    private String message;

}