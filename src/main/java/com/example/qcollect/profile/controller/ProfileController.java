package com.example.qcollect.profile.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.profile.dto.ChangePasswordRequest;
import com.example.qcollect.profile.dto.LoginActivityResponse;
import com.example.qcollect.profile.dto.ProfileResponse;
import com.example.qcollect.profile.dto.UpdateProfileRequest;
import com.example.qcollect.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<ProfileResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(profileService.getMyProfile(user.getUserId()))
                .build();
    }

    @PutMapping
    public ApiResponse<ProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateProfileRequest request
    ) {

        return ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(profileService.updateProfile(
                        user.getUserId(),
                        request))
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        profileService.changePassword(
                user.getUserId(),
                request
        );

        return ApiResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }
    @PostMapping("/photo")
    public ApiResponse<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        profileService.uploadProfilePhoto(
                user.getUserId(),
                file
        );

        return ApiResponse.builder()
                .success(true)
                .message("Profile photo updated.")
                .build();
    }
    @GetMapping("/login-activity")
    public ApiResponse<List<LoginActivityResponse>> loginActivity(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ApiResponse.<List<LoginActivityResponse>>builder()
                .success(true)
                .message("Login activity fetched successfully.")
                .data(profileService.getLoginActivities(user.getUserId()))
                .build();
    }
    @GetMapping("/login-history")
    public ApiResponse<List<LoginActivityResponse>> getLoginHistory(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ApiResponse.<List<LoginActivityResponse>>builder()
                .success(true)
                .message("Login history fetched.")
                .data(profileService.getLoginHistory(user.getUserId()))
                .build();
    }
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @AuthenticationPrincipal CustomUserDetails user) {

        profileService.logout(user.getUserId());

        return ApiResponse.builder()
                .success(true)
                .message("Logged out successfully.")
                .build();
    }
}