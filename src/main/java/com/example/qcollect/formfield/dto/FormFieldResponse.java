package com.example.qcollect.formfield.dto;

import com.example.qcollect.formfield.enums.FieldType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FormFieldResponse {

    private UUID id;

    private String label;

    private FieldType fieldType;

    private Boolean required;

    private String placeholder;

    private String defaultValue;

    private Integer fieldOrder;

    private String options;

    private String validationJson;

}