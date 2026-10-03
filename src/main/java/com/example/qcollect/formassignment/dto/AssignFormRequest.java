package com.example.qcollect.formassignment.dto;



import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignFormRequest {

    @NotNull
    private UUID accessorId;

    @NotNull
    private UUID qualityCheckerId;

}