package com.example.qcollect.notification.mapper;

import com.example.qcollect.notification.dto.NotificationResponse;
import com.example.qcollect.notification.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(
            Notification notification);

}