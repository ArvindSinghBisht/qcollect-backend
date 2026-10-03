package com.example.qcollect.integration.google.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleSpreadsheetResponse {

    private String spreadsheetId;

    private String spreadsheetName;

}