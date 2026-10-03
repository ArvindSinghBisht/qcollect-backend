package com.example.qcollect.formfield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFormFieldRequest {

    @NotBlank(message = "Field label is required.")
    @Size(
            min = 2,
            max = 100,
            message = "Field label must be between 2 and 100 characters."
    )
    private String label;

    @NotBlank(message = "Field name is required.")
    @Size(
            min = 2,
            max = 100,
            message = "Field name must be between 2 and 100 characters."
    )
    private String name;

    @NotBlank(message = "Field type is required.")
    private String fieldType;

    @NotNull(message = "Display order is required.")
    private Integer displayOrder;

    private Boolean required;

    private String placeholder;

    private String defaultValue;

    private String options;

    private String validationJson;

    public Integer getFieldOrder() {
        return displayOrder;
    }
}
