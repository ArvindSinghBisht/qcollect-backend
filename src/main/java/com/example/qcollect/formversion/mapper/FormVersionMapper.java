package com.example.qcollect.formversion.mapper;

import com.example.qcollect.formversion.dto.VersionDetailsResponse;
import com.example.qcollect.formversion.dto.VersionResponse;
import com.example.qcollect.formversion.entity.FormVersion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormVersionMapper {

    VersionResponse toResponse(FormVersion entity);

    VersionDetailsResponse toDetailsResponse(FormVersion entity);

}