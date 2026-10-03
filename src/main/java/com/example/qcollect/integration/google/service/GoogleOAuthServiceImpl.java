package com.example.qcollect.integration.google.service;

import com.example.qcollect.formfield.repository.FormFieldRepository;
import com.example.qcollect.integration.google.config.GoogleOAuthProperties;
import com.example.qcollect.integration.google.dto.*;
import com.example.qcollect.integration.google.entity.ProjectGoogleSheet;
import com.example.qcollect.integration.google.repository.ProjectGoogleSheetRepository;
import com.example.qcollect.submission.repository.SubmissionAnswerRepository;
import com.example.qcollect.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuthServiceImpl
        implements GoogleOAuthService {
    private final SubmissionRepository submissionRepository;

    private final SubmissionAnswerRepository submissionAnswerRepository;

    private final FormFieldRepository formFieldRepository;

    private final ProjectGoogleSheetRepository googleRepository;

    private final RestTemplate restTemplate;
//    private final RestTemplate restTemplate;

    private final ProjectGoogleSheetRepository repository;

    private final GoogleOAuthProperties properties;

    @Override
    public String generateAuthorizationUrl() {

//        String scope =
                String scope =
                "https://www.googleapis.com/auth/spreadsheets "
                        + "https://www.googleapis.com/auth/drive.readonly "
                        + "https://www.googleapis.com/auth/userinfo.email";

        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + properties.getClientId()
                + "&redirect_uri="
                + URLEncoder.encode(
                properties.getRedirectUri(),
                StandardCharsets.UTF_8
        )
                + "&response_type=code"
                + "&access_type=offline"
                + "&prompt=consent"
                + "&scope="
                + URLEncoder.encode(
                scope,
                StandardCharsets.UTF_8
        );
    }

    @Override
    public void connectProject(
            UUID projectId,
            String authorizationCode
    ) {

        String url =
                "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("code", authorizationCode);

        body.add("client_id",
                properties.getClientId());

        body.add("client_secret",
                properties.getClientSecret());

        body.add("redirect_uri",
                properties.getRedirectUri());

        body.add("grant_type",
                "authorization_code");

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTokenResponse> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        GoogleTokenResponse.class
                );

        GoogleTokenResponse token =
                response.getBody();
        GoogleUserResponse googleUser =
                getUserInfo(
                        token.getAccess_token()
                );
        if (token == null) {
            throw new RuntimeException(
                    "Failed to obtain Google Access Token."
            );
        }

        ProjectGoogleSheet googleSheet =
                repository.findByProjectId(projectId)
                        .orElse(new ProjectGoogleSheet());

        googleSheet.setProjectId(projectId);

        googleSheet.setAccessToken(
                token.getAccess_token()
        );

        googleSheet.setRefreshToken(
                token.getRefresh_token()
        );

        googleSheet.setConnected(true);

        googleSheet.setTokenExpiry(
                LocalDateTime.now()
                        .plusSeconds(
                                token.getExpires_in()
                        )
        );

        repository.save(googleSheet);
    }
    @Override
    public GoogleUserResponse getUserInfo(
            String accessToken
    ) {

        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response =
                restTemplate.exchange(

                        "https://www.googleapis.com/oauth2/v2/userinfo",

                        HttpMethod.GET,

                        request,

                        GoogleUserResponse.class

                );

        return response.getBody();

    }
    @Override
    public List<GoogleSpreadsheetResponse> getSpreadsheets(
            UUID projectId
    ) {

        ProjectGoogleSheet googleSheet =
                repository.findByProjectId(projectId)
                        .orElseThrow(() ->
                                new RuntimeException("Google not connected"));

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(
                googleSheet.getAccessToken()
        );

        HttpEntity<Void> entity =
                new HttpEntity<>(headers);

        String url =
                "https://www.googleapis.com/drive/v3/files"
                        + "?q=mimeType='application/vnd.google-apps.spreadsheet'"
                        + "&fields=files(id,name)";

        ResponseEntity<GoogleDriveResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        GoogleDriveResponse.class
                );

        return response.getBody()
                .getFiles()
                .stream()
                .map(file ->
                        GoogleSpreadsheetResponse.builder()
                                .spreadsheetId(file.getId())
                                .spreadsheetName(file.getName())
                                .build()
                )
                .toList();
    }
    @Override
    public void selectSpreadsheet(
            UUID projectId,
            SelectSpreadsheetRequest request
    ) {

        ProjectGoogleSheet googleSheet =
                repository.findByProjectId(projectId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Google not connected."
                                ));

        googleSheet.setSpreadsheetId(
                request.getSpreadsheetId()
        );

        googleSheet.setSpreadsheetName(
                request.getSpreadsheetName()
        );

        googleSheet.setSheetName(
                request.getSheetName()
        );

        repository.save(googleSheet);
    }
}