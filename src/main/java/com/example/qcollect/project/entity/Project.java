package com.example.qcollect.project.entity;



import com.example.qcollect.common.entity.BaseEntity;
//import com.qcollect.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


//import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    private String name;

    private String description;



}