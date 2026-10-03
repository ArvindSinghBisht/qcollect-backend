package com.example.qcollect.mobile.mapper;

import com.example.qcollect.mobile.dto.QueueResponse;
import com.example.qcollect.mobile.entity.SyncQueue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SyncQueueMapper {

    QueueResponse toResponse(SyncQueue entity);

}