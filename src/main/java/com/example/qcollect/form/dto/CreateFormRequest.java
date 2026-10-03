package com.example.qcollect.form.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CreateFormRequest {

    @NotBlank(message = "Form name is required.")
    @Size(
            min = 3,
            max = 100,
            message = "Form name must be between 3 and 100 characters."
    )
    private String name;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters."
    )
    private String description;

    @NotBlank(message = "Survey JSON is required.")
    private String surveyJson;
}