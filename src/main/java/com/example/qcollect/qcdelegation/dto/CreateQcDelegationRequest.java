package com.example.qcollect.qcdelegation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQcDelegationRequest {

    @NotNull
    private UUID formId;

    @NotNull
    private UUID accessorId;

    @NotNull
    private UUID delegateQcId;

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

    private String reason;
}