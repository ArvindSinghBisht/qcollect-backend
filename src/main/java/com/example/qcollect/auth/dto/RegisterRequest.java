package com.example.qcollect.auth.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String password;

    private String phone;

}