package com.example.qcollect.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccessorRequest {

    @NotBlank(message = "First name is required.")
    @Size(
            min = 2,
            max = 50,
            message = "First name must be between 2 and 50 characters."
    )
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters."
    )
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits."
    )
    private String phone;

    @NotBlank(message = "Password is required.")
    @Size(
            min = 8,
            max = 100,
            message = "Password must be between 8 and 100 characters."
    )
    private String password;
}