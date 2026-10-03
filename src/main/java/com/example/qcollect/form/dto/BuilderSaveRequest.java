
package com.example.qcollect.form.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuilderSaveRequest {

    @NotBlank(message = "Survey JSON is required.")
    private String surveyJson;

}