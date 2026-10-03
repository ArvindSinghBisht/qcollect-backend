package com.example.qcollect.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;

    private int status;

    private String error;

    private String message;

    private LocalDateTime timestamp;

    /**
     * Used for validation errors.
     * Null for normal exceptions.
     */
    private List<String> details;
}