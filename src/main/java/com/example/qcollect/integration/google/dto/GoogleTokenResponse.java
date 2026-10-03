package com.example.qcollect.integration.google.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleTokenResponse {

    private String access_token;

    private String refresh_token;

    private Integer expires_in;

    private String token_type;

}