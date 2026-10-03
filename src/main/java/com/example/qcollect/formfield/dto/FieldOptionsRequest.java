package com.example.qcollect.formfield.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldOptionsRequest {

    @NotEmpty
    private List<String> options;

}