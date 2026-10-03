package com.example.qcollect.auth.service;

import com.example.qcollect.auth.dto.LoginRequest;
import com.example.qcollect.auth.dto.LoginResponse;
import com.example.qcollect.auth.dto.RegisterRequest;
import com.example.qcollect.auth.security.JwtService;
import com.example.qcollect.common.exception.BadRequestException;
import com.example.qcollect.profile.entity.LoginActivity;
import com.example.qcollect.profile.repository.LoginActivityRepository;
import com.example.qcollect.project.dto.ProjectMembershipResponse;
import com.example.qcollect.project.repository.ProjectUserRepository;
import com.example.qcollect.role.entity.Role;
import com.example.qcollect.role.repository.RoleRepository;
import com.example.qcollect.user.entity.User;
import com.example.qcollect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final LoginActivityRepository loginActivityRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final ProjectUserRepository projectUserRepository;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists.");
        }

        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() ->
                        new BadRequestException("SUPER_ADMIN role not found."));

        if (userRepository.existsBySystemRole(superAdminRole)) {
            throw new BadRequestException(
                    "Platform already initialized. Contact the Super Admin."
            );
        }

        User user = new User();

        user.setTenantId(null);
        user.setSystemRole(superAdminRole);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("User not found."));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BadRequestException("User account is inactive.");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Save login history
        LoginActivity activity = LoginActivity.builder()
                .userId(user.getId())
                .loginTime(LocalDateTime.now())
                .ipAddress(null)
                .device(null)
                .browser(null)
                .operatingSystem(null)
                .activeSession(true)
                .build();

        loginActivityRepository.save(activity);

        loginActivityRepository.save(activity);

        String token = jwtService.generateToken(user);

        List<ProjectMembershipResponse> projects =
                projectUserRepository
                        .findActiveMembershipsByUserId(user.getId())
                        .stream()
                        .map(membership ->
                                ProjectMembershipResponse.builder()
                                        .projectId(membership.getProjectId())
                                        .projectName(membership.getProjectName())
                                        .role(membership.getRoleName())
                                        .build()
                        )
                        .toList();

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .systemRole(user.getSystemRole().getName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .projects(projects)
                .build();
    }
}
