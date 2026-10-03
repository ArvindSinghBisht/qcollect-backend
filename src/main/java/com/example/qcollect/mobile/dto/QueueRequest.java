package com.example.qcollect.mobile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueRequest {

    @NotNull
    private UUID submissionId;

    @NotBlank
    private String payload;
}