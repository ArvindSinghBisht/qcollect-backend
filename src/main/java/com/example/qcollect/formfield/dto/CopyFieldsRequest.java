package com.example.qcollect.formfield.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyFieldsRequest {

    @NotNull
    private UUID sourceFormId;

}