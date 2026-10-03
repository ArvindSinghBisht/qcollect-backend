package com.example.qcollect.profile.service;

import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.common.exception.ResourceNotFoundException;
import com.example.qcollect.profile.dto.ChangePasswordRequest;
import com.example.qcollect.profile.dto.LoginActivityResponse;
import com.example.qcollect.profile.dto.ProfileResponse;
import com.example.qcollect.profile.dto.UpdateProfileRequest;
import com.example.qcollect.profile.entity.LoginActivity;
import com.example.qcollect.profile.repository.LoginActivityRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${file.upload-dir}")
    private String uploadDir;
//    public ProfileServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Override
    public ProfileResponse getMyProfile(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return ProfileResponse.builder()

                .id(user.getId())

                .firstName(user.getFirstName())

                .lastName(user.getLastName())

                .email(user.getEmail())

                .phone(user.getPhone())

                .profilePhoto(user.getProfilePhoto())

                .emailVerified(user.getEmailVerified())

                .phoneVerified(user.getPhoneVerified())

                .active(user.getActive())
                .lastLogin(user.getLastLogin())
                .build();
    }
    @Override
    public void uploadProfilePhoto(
            UUID userId,
            MultipartFile file
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please select a file.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Maximum file size is 5 MB.");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null) {
            throw new BadRequestException("Invalid file.");
        }

        String extension = "";

        int dot = originalName.lastIndexOf(".");

        if (dot > 0) {
            extension = originalName.substring(dot + 1).toLowerCase();
        }

        if (!extension.equals("jpg")
                && !extension.equals("jpeg")
                && !extension.equals("png")
                && !extension.equals("webp")) {

            throw new BadRequestException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed."
            );
        }

        try {

            Path directory = Paths.get(uploadDir, "profile");

            Files.createDirectories(directory);

            String fileName = UUID.randomUUID() + "." + extension;

            Path target = directory.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            user.setProfilePhoto("/uploads/profile/" + fileName);

            userRepository.save(user);

        } catch (IOException e) {

            throw new BadRequestException(
                    "Unable to upload profile photo."
            );
        }
    }
    @Override
    public ProfileResponse updateProfile(
            UUID userId,
            UpdateProfileRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());

        user.setLastName(request.getLastName());

        user.setPhone(request.getPhone());

        userRepository.save(user);

        return getMyProfile(userId);
    }
    @Override
    public void changePassword(
            UUID userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword()
        )) {

            throw new BadRequestException("Current password is incorrect.");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }
    @Override
    public List<LoginActivityResponse> getLoginActivities(UUID userId) {

        return loginActivityRepository
                .findByUserIdOrderByLoginTimeDesc(userId)
                .stream()
                .map(activity ->
                        LoginActivityResponse.builder()
                                .device(activity.getDevice())
                                .browser(activity.getBrowser())
                                .operatingSystem(activity.getOperatingSystem())
                                .ipAddress(activity.getIpAddress())
                                .loginTime(activity.getLoginTime())
                                .logoutTime(activity.getLogoutTime())
                                .activeSession(activity.getActiveSession())
                                .build())
                .toList();
    }
    @Override
    public List<LoginActivityResponse> getLoginHistory(UUID userId) {

        return loginActivityRepository
                .findByUserIdOrderByLoginTimeDesc(userId)
                .stream()
                .map(activity -> LoginActivityResponse.builder()
                        .id(activity.getId())
                        .browser(activity.getBrowser())
                        .device(activity.getDevice())
                        .operatingSystem(activity.getOperatingSystem())
                        .ipAddress(activity.getIpAddress())
                        .loginTime(activity.getLoginTime())
                        .logoutTime(activity.getLogoutTime())
                        .activeSession(activity.getActiveSession())
                        .build())
                .toList();
    }
    @Override
    public void logout(UUID userId) {

        LoginActivity activity =
                loginActivityRepository
                        .findByUserIdAndActiveSessionTrue(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No active session found."
                                ));

        activity.setActiveSession(false);

        activity.setLogoutTime(LocalDateTime.now());

        loginActivityRepository.save(activity);
    }
}

