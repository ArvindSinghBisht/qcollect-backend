package com.example.qcollect.integration.google.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleSheetStatusResponse {

    private Boolean connected;

    private String googleEmail;

    private String spreadsheetName;

    private String sheetName;

}
