package com.example.qcollect.form.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveDraftRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String surveyJson;

}