package com.example.qcollect.integration.google.entity;

import com.example.qcollect.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "project_google_sheets")
public class ProjectGoogleSheet extends BaseEntity {

    private UUID projectId;

    private String googleEmail;

    private String spreadsheetId;

    private String spreadsheetName;

    private String sheetName;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime tokenExpiry;

    private Boolean connected;

}