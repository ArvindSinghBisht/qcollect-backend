package com.example.qcollect.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain 10 digits."
    )
    private String phone;
}