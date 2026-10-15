package com.example.qcollect.project.dto.request;

import com.example.qcollect.common.jackson.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteUserRequest {


    @Email
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String email;


    private UUID roleId;

}
