package com.example.qcollect.integration.google.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserResponse {

    private String email;

    private Boolean verified_email;

}