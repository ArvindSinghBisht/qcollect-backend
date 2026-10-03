package com.example.qcollect.submission.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FlagQuestionRequest {

    @NotNull
    private UUID answerId;

    @NotBlank
    private String comment;
}
