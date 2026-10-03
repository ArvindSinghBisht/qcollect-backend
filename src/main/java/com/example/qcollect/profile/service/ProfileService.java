package com.example.qcollect.profile.service;

import com.example.qcollect.profile.dto.ChangePasswordRequest;
import com.example.qcollect.profile.dto.LoginActivityResponse;
import com.example.qcollect.profile.dto.ProfileResponse;
import com.example.qcollect.profile.dto.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileResponse getMyProfile(UUID userId);

    ProfileResponse updateProfile(
            UUID userId,
            UpdateProfileRequest request
    );
    void uploadProfilePhoto(
            UUID userId,
            MultipartFile file
    );
    void changePassword(
            UUID userId,
            ChangePasswordRequest request
    );
    List<LoginActivityResponse> getLoginActivities(UUID userId);
    List<LoginActivityResponse> getLoginHistory(UUID userId);

    void logout(UUID userId);
}