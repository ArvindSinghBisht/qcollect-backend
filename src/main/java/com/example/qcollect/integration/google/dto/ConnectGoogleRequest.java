package com.example.qcollect.integration.google.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectGoogleRequest {

    @NotBlank
    private String authorizationCode;

}