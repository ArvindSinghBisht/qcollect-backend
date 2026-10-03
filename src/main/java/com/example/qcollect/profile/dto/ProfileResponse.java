package com.example.qcollect.profile.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProfileResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String profilePhoto;

    private Boolean emailVerified;

    private Boolean phoneVerified;

    private Boolean active;
    private LocalDateTime lastLogin;
}
