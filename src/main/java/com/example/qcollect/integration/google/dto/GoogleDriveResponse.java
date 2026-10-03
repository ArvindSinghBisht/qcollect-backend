package com.example.qcollect.integration.google.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoogleDriveResponse {

    private List<GoogleFile> files;

}