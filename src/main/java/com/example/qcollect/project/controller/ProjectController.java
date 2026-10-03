package com.example.qcollect.project.controller;

import com.example.qcollect.auth.security.CustomUserDetails;
import com.example.qcollect.common.dto.ApiResponse;
import com.example.qcollect.project.dto.*;
import com.example.qcollect.project.dto.request.InviteUserRequest;
import com.example.qcollect.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ApiResponse<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<ProjectResponse>builder()
                .success(true)
                .message("Project created successfully.")
                .data(projectService.createProject(request, user.getUserId()))
                .build();
    }

    @PostMapping("/{projectId}/admins")
    public ApiResponse<?> createProjectAdmin(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectAdminRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        projectService.createProjectAdmin(
                projectId,
                request,
                user.getUserId()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Project Admin created successfully.")
                .build();
    }

    @PostMapping("/{projectId}/accessors")
    public ApiResponse<?> createAccessor(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateAccessorRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        projectService.createAccessor(
                projectId,
                request,
                user.getUserId()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Accessor created successfully.")
                .build();
    }

    @PostMapping("/{projectId}/quality-checkers")
    public ApiResponse<?> createQualityChecker(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateQualityCheckerRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        projectService.createQualityChecker(
                projectId,
                request,
                user.getUserId()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Quality Checker created successfully.")
                .build();
    }

    @PostMapping("/{projectId}/members")
    public ApiResponse<ProjectMemberResponse> addMember(
            @PathVariable UUID projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .success(true)
                .message("Member added successfully.")
                .data(projectService.addMember(
                        projectId,
                        request,
                        user.getUserId()
                ))
                .build();
    }

    @GetMapping("/{projectId}/members")
    public ApiResponse<List<ProjectMemberResponse>> getMembers(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<List<ProjectMemberResponse>>builder()
                .success(true)
                .message("Members fetched successfully.")
                .data(projectService.getProjectMembers(
                        projectId,
                        user.getUserId()
                ))
                .build();
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ApiResponse<?> removeMember(
            @PathVariable UUID projectId,
            @PathVariable UUID memberId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        projectService.removeMember(
                projectId,
                memberId,
                user.getUserId()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Member removed successfully.")
                .build();
    }

    @GetMapping("/my-projects")
    public ApiResponse<List<MyProjectResponse>> getMyProjects(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.<List<MyProjectResponse>>builder()
                .success(true)
                .message("Projects fetched successfully.")
                .data(projectService.getMyProjects(user.getUserId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects() {
        return ResponseEntity.ok(
                ApiResponse.<List<ProjectResponse>>builder()
                        .success(true)
                        .message("Projects fetched successfully.")
                        .data(projectService.getProjects())
                        .build()
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<ProjectResponse>builder()
                        .success(true)
                        .message("Project fetched successfully.")
                        .data(projectService.getProject(projectId))
                        .build()
        );
    }

    @PostMapping("/{projectId}/invite")
    public ApiResponse<?> inviteUser(
            @PathVariable UUID projectId,
            @Valid @RequestBody InviteUserRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {


        projectService.inviteUser(
                projectId,
                request,
                user.getUserId()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Invitation sent successfully.")
                .build();
    }
}