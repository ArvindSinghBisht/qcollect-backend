package com.example.qcollect.integration.google.service;

import com.example.qcollect.integration.google.dto.GoogleSpreadsheetResponse;
import com.example.qcollect.integration.google.dto.GoogleUserResponse;
import com.example.qcollect.integration.google.dto.SelectSpreadsheetRequest;

import java.util.List;
import java.util.UUID;

public interface GoogleOAuthService {

    String generateAuthorizationUrl();
    void connectProject(
            UUID projectId,
            String authorizationCode
    );
    GoogleUserResponse getUserInfo(String accessToken);
    List<GoogleSpreadsheetResponse> getSpreadsheets(
            UUID projectId
    );
    void selectSpreadsheet(
            UUID projectId,
            SelectSpreadsheetRequest request
    );
}