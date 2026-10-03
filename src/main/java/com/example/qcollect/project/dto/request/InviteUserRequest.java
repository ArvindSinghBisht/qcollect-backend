package com.example.qcollect.project.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteUserRequest {


    @Email
    private String email;


    private UUID roleId;

}
