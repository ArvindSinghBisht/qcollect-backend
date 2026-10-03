package com.example.qcollect.integration.google.controller;

import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.integration.google.dto.ConnectGoogleRequest;
import com.example.qcollect.integration.google.dto.GoogleSpreadsheetResponse;
import com.example.qcollect.integration.google.dto.SelectSpreadsheetRequest;
import com.example.qcollect.integration.google.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/google")
@RequiredArgsConstructor
public class GoogleIntegrationController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/authorize")
    public ApiResponse<String> authorize() {

        return ApiResponse.<String>builder()
                .success(true)
                .message("Authorization URL generated.")
                .data(googleOAuthService.generateAuthorizationUrl())
                .build();
    }
    @PostMapping("/{projectId}/connect")
    public ApiResponse<?> connect(

            @PathVariable UUID projectId,

            @RequestBody ConnectGoogleRequest request
    ) {

        googleOAuthService.connectProject(
                projectId,
                request.getAuthorizationCode()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Google Connected Successfully")
                .build();

    }
    @GetMapping("/{projectId}/spreadsheets")
    public ApiResponse<List<GoogleSpreadsheetResponse>>
    getSheets(

            @PathVariable UUID projectId
    ) {

        return ApiResponse
                .<List<GoogleSpreadsheetResponse>>builder()
                .success(true)
                .message("Sheets fetched successfully")
                .data(
                        googleOAuthService.getSpreadsheets(projectId)
                )
                .build();
    }
    @PostMapping("/{projectId}/spreadsheet")
    public ApiResponse<?> selectSpreadsheet(

            @PathVariable UUID projectId,

            @RequestBody SelectSpreadsheetRequest request

    ) {

        googleOAuthService.selectSpreadsheet(
                projectId,
                request
        );

        return ApiResponse.builder()
                .success(true)
                .message("Spreadsheet selected successfully.")
                .build();

    }
}