package com.example.qcollect.project.dto;

import com.example.qcollect.common.jackson.TrimStringDeserializer;
import com.example.qcollect.project.enums.ProjectRole;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImportProjectMemberRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    @NotBlank
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String email;

    private String phone;

    private String password;

    @NotNull
    private ProjectRole role;
}
