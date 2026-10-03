package com.example.qcollect.formfield.mapper;

import com.example.qcollect.formfield.dto.FormFieldResponse;
import com.example.qcollect.formfield.entity.FormField;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormFieldMapper {

    FormFieldResponse toResponse(FormField formField);

}