package com.example.qcollect.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required.")
    @Size(
            min = 3,
            max = 100,
            message = "Project name must be between 3 and 100 characters."
    )
    private String name;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters."
    )
    private String description;

}