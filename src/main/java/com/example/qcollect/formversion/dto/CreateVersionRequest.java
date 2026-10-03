package com.example.qcollect.formversion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVersionRequest {

    @NotBlank
    private String surveyJson;

}