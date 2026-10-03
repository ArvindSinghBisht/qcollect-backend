package com.example.qcollect.formfield.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderFieldRequest {

    @NotNull
    private UUID fieldId;

    @NotNull
    private Integer fieldOrder;
}