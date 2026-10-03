package com.example.qcollect.integration.google.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectSpreadsheetRequest {

    private String spreadsheetId;

    private String spreadsheetName;

    private String sheetName;

}