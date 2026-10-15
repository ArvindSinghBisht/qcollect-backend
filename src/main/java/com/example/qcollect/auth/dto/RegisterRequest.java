package com.example.qcollect.auth.dto;



import com.example.qcollect.common.jackson.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String email;

    @NotBlank
    private String password;

    private String phone;

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

}