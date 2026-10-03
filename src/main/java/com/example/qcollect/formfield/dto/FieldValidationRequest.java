package com.example.qcollect.formfield.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidationRequest {

    private Integer minLength;

    private Integer maxLength;

    private Double minValue;

    private Double maxValue;

    private String regex;

    private String validationMessage;
}