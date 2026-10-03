package com.example.qcollect.form.mapper;

import com.example.qcollect.form.dto.FormResponse;
import com.example.qcollect.form.entity.Form;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "surveyJson", source = "surveyJson")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "published", source = "published")
    FormResponse toResponse(Form form);
}