package com.example.qcollect.project.entity;

import com.example.qcollect.common.entity.BaseEntity;
import com.example.qcollect.role.entity.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="project_invitations")
@Getter
@Setter
public class ProjectInvitation extends BaseEntity {


    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;


    private String email;


    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;


    private String token;


    private String status;


    private LocalDateTime expiresAt;


    private LocalDateTime acceptedAt;

}